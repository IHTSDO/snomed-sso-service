package org.snomed.ssoservice.rest

import org.snomed.ssoservice.rest.dto.UserDTO
import org.snomed.ssoservice.service.CrowdRestClient
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestClientException
import spock.lang.Specification
import spock.lang.Title

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Title("Account Controller Specification")
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
@ContextConfiguration
class AccountControllerTest extends Specification {
    @Autowired
    private MockMvc mockMvc

    @SpringBean
    CrowdRestClient crowdRestClient = Mock()

    def "private healthcheck endpoint should return 401/UNAUTHORIZED when not authenticated"() {
        when:
            def requestBuilder = get("/api/health")
            def response = mockMvc.perform(requestBuilder).andReturn().response

        then:
            response.status == HttpStatus.UNAUTHORIZED.value()
    }

    def "private healthcheck endpoint should return 200/OK when authenticated"() {
        when:
            def requestBuilder = get("/api/health")
                    .with(httpBasic("user", "password"))
            def response = mockMvc.perform(requestBuilder).andReturn().response

        then:
            response.status == HttpStatus.OK.value()
            response.contentAsString == "Authenticated healthcheck login OK"
    }

    def "validate authenticate endpoint should return 200/OK"() {
        when:
            def jsonPayload = '{"login": "user", "password": "password"}'

            def requestBuilder = get("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilder).andReturn().response

        then:
            response.status == HttpStatus.OK.value()
    }

    def "validate authenticate endpoint should return 400/BAD_REQUEST when user auth is invalid: #title"() {
        when:
            def jsonPayload = '{"login": ' + login + ', "password": ' + password + '}'

            def requestBuilder = post("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilder).andReturn().response

        then:
            response.status == HttpStatus.BAD_REQUEST.value()

        where:
            title                       | login       | password
            "All invalid"               | ""          | ""
            "All null"                  | null        | null
            "empty password"            | "user"      | ""
            "null password"             | "user"      | null
            "empty login"               | ""          | "password"
            "null login"                | null        | "password"
            "Invalid user and password" | "carlsagan" | "WrongAttempt"
    }

    def "validate authenticate endpoint when authentication fails with exception response should return 404/NOT_FOUND"() {
        given: "A real user"
            def jsonPayload = '{"login": "realuser", "password": "realpassword"}'

        and: "Mocked crowdRestClient response"
            1 * crowdRestClient.authenticate(_, _) >> { throw new RestClientException("Authentication failed") }

        when:
            def requestBuilder = post("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilder).andReturn().response

        then:
            response.status == HttpStatus.NOT_FOUND.value()
    }

    def "validate authenticate endpoint should return 200/OK when user is valid"() {
        given: "A real user"
            def jsonPayload = '{"login": "realuser", "password": "realpassword"}'

        and: "Mocked crowdRestClient response"
            1 * crowdRestClient.authenticate(_, _) >> "SomeToken"

        when:
            def requestBuilder = post("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilder).andReturn().response
            def cookie = response.getCookie("dev-ims-ihtsdo")

        then:
            response.status == HttpStatus.OK.value()
            cookie.getValue() == "SomeToken"
            cookie.getName() == "dev-ims-ihtsdo"
            cookie.getMaxAge() == 259200
            cookie.getDomain() == "ihtsdotools.org"
            cookie.getPath() == "/"
            !cookie.getSecure()
    }


    def "validate /account/logout endpoint should return 200/OK when user is already logged in"() {
        given: "A real user"
            def jsonPayload = '{"login": "realuser", "password": "realpassword"}'

        and: "Mocked crowdRestClient response"
            1 * crowdRestClient.authenticate(_, _) >> "SomeToken"

        when: "Login"
            def requestBuilderForAuth = post("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def responseForAuth = mockMvc.perform(requestBuilderForAuth).andReturn().response
            def cookie = responseForAuth.getCookie("dev-ims-ihtsdo")

        and:  "Then logout"
            def requestBuilderForAccountLogout = post("/api/account/logout")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilderForAccountLogout).andReturn().response

        then:
            responseForAuth.status == HttpStatus.OK.value()
            cookie.getValue() == ""
            cookie.getName() == "dev-ims-ihtsdo"
            cookie.getMaxAge() == 0
            cookie.getDomain() == "ihtsdotools.org"
            cookie.getPath() == "/"
            !cookie.getSecure()
    }

    def "validate /account/logout endpoint should return 200/OK and cookie is null"() {
        when: "Logout without auth (should never happen!)"
            def requestBuilderForAccountLogout = post("/api/account/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilderForAccountLogout).andReturn().response
            def cookie = response.getCookie("dev-ims-ihtsdo")

        then:
            response.status == HttpStatus.OK.value()
            cookie == null
    }

    def "validate /account endpoint should return 200/OK"() {
        given: "A real user"
            def jsonPayload = '{"login": "realuser", "password": "realpassword"}'
            def userDTO = new UserDTO('joe', 'asecret', 'Joe', 'Smith', 'joe@example.com', 'someLangKey', null)

        and: "Mocked crowdRestClient response"
            1 * crowdRestClient.authenticate(_, _) >> "SomeToken"
            1 * crowdRestClient.getUserByToken(_) >> userDTO

        when: "Login"
            def requestBuilderForAuth = post("/api/authenticate")
                    .content(jsonPayload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def responseForAuth = mockMvc.perform(requestBuilderForAuth).andReturn().response
            def cookie = responseForAuth.getCookie("dev-ims-ihtsdo")

        and: "Then get account info"
            def requestBuilderForAccount = get("/api/account")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)

            def response = mockMvc.perform(requestBuilderForAccount).andReturn().response
            def cookie2 = response.getCookie("dev-ims-ihtsdo")

        then:
            response.status == HttpStatus.OK.value()
            response.contentAsString.contains("login")
            response.contentAsString.contains("joe")
            cookie.getName() == "dev-ims-ihtsdo"
            cookie.getValue() == "SomeToken"
            cookie2 == null
    }
}
