package org.snomed.ssoservice.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.ssoservice.rest.dto.UserDTO;
import org.snomed.ssoservice.security.AuthoritiesConstants;
import org.snomed.ssoservice.service.model.GroupsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("rawtypes")
public class CrowdRestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrowdRestClient.class);

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Value("${crowd.api.url}")
    private String crowdApiUrl;

    @Value("${crowd.api.auth.application-name}")
    private String crowdApiUsername;

    @Value("${crowd.api.auth.application-password}")
    private String crowdApiPassword;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplateBuilder()
                .rootUri(crowdApiUrl)
                .basicAuthentication(crowdApiUsername, crowdApiPassword)
                .build();
    }

    public void getGroups(String username) {
        LOGGER.info("getGroups for user: {}", username);
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, username);
        GroupsResponse groupsResponse = restTemplate.getForObject("/user/group/direct.json?username={username}", GroupsResponse.class, params);

        if (groupsResponse == null) {
            LOGGER.warn("Group response is null");
        } else {
            LOGGER.info("Group names: {}", groupsResponse.getGroupNames());
        }
    }

    public void getUser(String username) {
        LOGGER.info("getUser: {}", username);
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, username);
        UserDTO userDTO = restTemplate.getForObject("/user?username={username}", UserDTO.class, params);

        if (userDTO == null) {
            LOGGER.info("User not found: {}", username);
        } else {
            LOGGER.info(userDTO.getEmail());
        }
    }

    public String authenticate(String username, String password) {
        LOGGER.info("authenticate: {}", username);
        Map<String, String> params = new HashMap<>();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);

        Map response = restTemplate.postForObject("/session", params, Map.class);

        if (null != response) {
            return (String) response.get("token");
        }

        return "";
    }

    @Cacheable(value="accountCache", key="#token")
    public UserDTO getUserByToken(String token) {
        LOGGER.debug("getUserByToken: token={}", token);

        UserDTO userDTO = new UserDTO();
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        Map result = restTemplate.getForObject("/session/{token}", Map.class, params);

        if (null != result) {
            // Get user information
            Map user = (Map) result.get("user");
            LOGGER.debug("getUserByToken: key={}", user.get("key"));
            userDTO.setFirstName(user.get("first-name").toString());
            userDTO.setLastName(user.get("last-name").toString());
            userDTO.setEmail(user.get("email").toString());
            userDTO.setLangKey(user.get("key").toString());
            userDTO.setLogin(user.get("name").toString());

            // Get all roles of user
            params.clear();
            params.put(USERNAME, user.get("name").toString());
            result = restTemplate.getForObject("/user/group/direct?username={username}", Map.class, params);

            if (null != result) {
                ArrayList<?> arrRoles = (ArrayList<?>) result.get("groups");
                List<String> lstRoles = new ArrayList<>();

                for (Object arrRole : arrRoles) {
                    Map role = (Map) arrRole;
                    lstRoles.add(AuthoritiesConstants.ROLE_PREFIX + role.get("name"));
                }

                userDTO.setRoles(lstRoles);
            }
        }

        return userDTO;
    }

    @CacheEvict(value = "accountCache", key = "#token")
    public void invalidateToken(String token) {
        LOGGER.debug("invalidateToken: {}", token);
        restTemplate.delete("/session/{token}", token);
    }
}
