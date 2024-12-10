package org.snomed.ssoservice.rest;

import org.snomed.ssoservice.rest.dto.UserDTO;
import org.snomed.ssoservice.service.CrowdRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    private CrowdRestClient crowdRestClient;

    @GetMapping(value = "/{username}/details",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable String username) {
        try {
            UserDTO user = crowdRestClient.getUser(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RestClientException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
