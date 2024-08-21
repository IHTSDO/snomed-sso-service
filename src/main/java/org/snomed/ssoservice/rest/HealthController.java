package org.snomed.ssoservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);
    public static final String LOGIN_OK = "Public healthcheck OK";

    @GetMapping(value = "/health")
    @Operation(summary = "Public health check, does nothing other than confirm you can ping the service")
    public String healthcheck() {
        LOGGER.info(LOGIN_OK);
        return LOGIN_OK;
    }
}
