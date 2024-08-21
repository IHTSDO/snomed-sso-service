package org.snomed.ssoservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.ssoservice.rest.base.BaseController;
import org.snomed.ssoservice.rest.dto.UserDTO;
import org.snomed.ssoservice.service.CrowdRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api")
public class AccountController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Value("${cookie.name}")
    private String cookieName;

    @Value("${cookie.maxAge}")
    private int cookieMaxAge;

    @Value("${cookie.domain}")
    private String cookieDomain;

    @Value("${cookie.secure}")
    private boolean cookieSecureFlag;

    private final CrowdRestClient crowdRestClient;

    public static final String AUTH_HEADER_USERNAME = "X-AUTH-username";
    public static final String AUTH_HEADER_ROLES = "X-AUTH-roles";
    public static final String LOGIN_OK = "Authenticated healthcheck login OK";

    public AccountController(CrowdRestClient crowdRestClient) {
        this.crowdRestClient = crowdRestClient;
    }

    /**
     * Performs a health check by logging a message and returning a string.
     *
     * @return the message "login ok" indicating that the health check was successful
     */
    @GetMapping(value = "/health")
    @Operation(summary = "Private health check, does nothing other than confirm you have authenticated")
    public String healthCheck() {
        LOGGER.debug(LOGIN_OK);
        return LOGIN_OK;
    }

    /**
     * GET  /authenticate -> check if the user is authenticated, and return their login.
     * Checks if the current user is authenticated.
     *
     * @param request the HttpServletRequest object representing the current request
     * @return the username of the authenticated user, or null if user is not authenticated
     */
    @GetMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Check if the user is authenticated, and return their login")
    public String isAuthenticated(HttpServletRequest request) {
        LOGGER.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * POST  /authenticate -> validate user.
     * Validates a user by authenticating the provided credentials and setting a cookie with the authentication token.
     *
     * @param dto      the UserDTO object containing the user's login and password
     * @param response the HttpServletResponse object to add the authentication token cookie to
     * @return a ResponseEntity with HTTP status code OK if the user is valid, or HTTP status code BAD_REQUEST or NOT_FOUND if the user is invalid
     */
    @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Validates a user by authenticating the provided credentials and setting a cookie with the authentication token")
    public ResponseEntity<Void> validateUser(@RequestBody UserDTO dto, HttpServletResponse response) {
        LOGGER.debug("Post rest request to login: {}", dto.getLogin());

        if (StringUtils.isBlank(dto.getLogin()) || StringUtils.isBlank(dto.getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            String token = crowdRestClient.authenticate(dto.getLogin(), dto.getPassword());
            Cookie cookie = new Cookie(cookieName, token);
            cookie.setMaxAge(cookieMaxAge);
            cookie.setDomain(cookieDomain);
            cookie.setSecure(cookieSecureFlag);
            cookie.setPath("/");
            response.addCookie(cookie);
            LOGGER.debug("Post rest request to login successful: {}", dto.getLogin());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RestClientException ex) {
            LOGGER.error("Error in /authenticate: {} : {}", ex.getMessage(), ex.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /account/logout  --> logout
     * Logs out the current user by invalidating the session and removing the authentication token cookie.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     */
    @PostMapping(value = "/account/logout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Logs out the current user by invalidating the session and removing the authentication token cookie")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("Post rest request to logout");

        HttpSession session = request.getSession(false);
        if (request.isRequestedSessionIdValid() && session != null) {
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName) && cookie.getMaxAge() != 0) {
                if (StringUtils.isNotEmpty(cookie.getValue())) {
                    try {
                        crowdRestClient.invalidateToken(cookie.getValue());
                        LOGGER.debug("Post rest request to logout successful");
                    } catch (RestClientException ex) {
                        LOGGER.error("Token {} is not valid", cookie.getValue());
                    }
                }

                invalidateCookieAndAddToResponse(response, cookie);
            }
        }
    }

    /**
     * GET  /account -> get the current user.
     * Retrieves the user account information.
     *
     * @param request  the HttpServletRequest object representing the current request
     * @param response the HttpServletResponse object representing the current response
     * @return a ResponseEntity with HTTP status code OK and the UserDTO object if the user is authenticated,
     * or HTTP status code FORBIDDEN if the user is not authenticated
     */
    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the user account information")
    public ResponseEntity<UserDTO> getAccount(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("Get rest request to get info on current user");

        UserDTO userDTO;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName) && cookie.getMaxAge() != 0) {
                    try {
                        userDTO = crowdRestClient.getUserByToken(cookie.getValue());
                    } catch (RestClientException ex) {
                        LOGGER.error("Error in /account: {} : {}", ex.getMessage(), ex.getStackTrace());
                        invalidateCookieAndAddToResponse(response, cookie);
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }

                    // Set response header
                    response.setHeader("Content-Type", "application/json;charset=UTF-8");
                    response.setHeader(AUTH_HEADER_USERNAME, userDTO.getLogin());
                    response.setHeader(AUTH_HEADER_ROLES, StringUtils.join(userDTO.getRoles(), ","));

                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
