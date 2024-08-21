package org.snomed.ssoservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.ssoservice.rest.base.BaseController;
import org.snomed.ssoservice.rest.dto.UserDTO;
import org.snomed.ssoservice.security.AuthoritiesConstants;
import org.snomed.ssoservice.service.CrowdRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing cache.
 * <br/>
 * <br/>
 * This class represents a REST API resource for managing cache operations.
 * It provides methods to clear the cache for all users.
 */
@RestController
@RequestMapping("/api")
public class CacheController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);

    @Value("${cookie.name}")
    private String cookieName;

    private final CrowdRestClient crowdRestClient;
    private final CacheManager accountCacheManager;

    public CacheController(CrowdRestClient crowdRestClient, CacheManager accountCacheManager) {
        this.crowdRestClient = crowdRestClient;
        this.accountCacheManager = accountCacheManager;
    }

    /**
     * POST  /cache/clear-all -> clear cache for all users.
     * <br/>
     * <br/>
     * Clears the cache for all users.
     * <br/>
     * <br/>
     * This method clears the cache for all users. It first checks the provided HTTP request for a valid cookie. If a valid cookie is found, it validates the user associated with
     * the cookie. If the user is an IMS_ADMIN, it clears the cache and logs the action. If the user is not authorized or an error occurs during the process, appropriate exceptions
     * are thrown.
     *
     * @param request  The HTTP request containing the cookies.
     * @param response The HTTP response to update the cookies if needed.
     * @throws RestClientException     If an error occurs while communicating with the Crowd REST client.
     * @throws ResponseStatusException If the user is not authorized or an error occurs. The exception contains the appropriate HTTP status and error message.
     * @throws NullPointerException    If the request or response is null.
     */
    @PostMapping(value = "/cache/clear-all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Post request to clear the cache for all users")
    public void clearCache(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("POST REST request to clear cache");
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName) && cookie.getMaxAge() != 0) {
                    try {
                        UserDTO user = crowdRestClient.getUserByToken(cookie.getValue());

                        if (user.getRoles().contains(AuthoritiesConstants.IMS_ADMIN)) {
                            clearAllCaches(user);
                            return;
                        }
                    } catch (RestClientException restClientException) {
                        LOGGER.error("Error in /cache/clear-all: {}", restClientException.getMessage(), restClientException);
                        invalidateCookieAndAddToResponse(response, cookie);
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, restClientException.getMessage(), restClientException);
                    }
                }
            }
        }

        LOGGER.error("Not authorised, user needs {} role", AuthoritiesConstants.IMS_ADMIN);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private void clearAllCaches(UserDTO user) {
        LOGGER.info("User {} clearing caches", user.getLogin());
        accountCacheManager.getCacheNames().parallelStream().forEach(this::clearSingleCacheByName);
    }

    private void clearSingleCacheByName(String name) {
        LOGGER.info("Clearing cache named : {}", name);
        var cache = accountCacheManager.getCache(name);

        if (cache != null) {
            cache.clear();
        }
    }
}
