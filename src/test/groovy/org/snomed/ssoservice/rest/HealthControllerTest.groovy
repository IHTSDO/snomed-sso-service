package org.snomed.ssoservice.rest


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Title

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Title("Health Controller Specification")
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
@ContextConfiguration
class HealthControllerTest extends Specification {
    @Autowired
    private MockMvc mockMvc

    def "when GET is performed on public health check the response has status 200 and content is correct"() {
        when:
            def response = mockMvc.perform(get("/health")).andReturn().response

        then:
            response.status == HttpStatus.OK.value()
            response.contentAsString == "Public healthcheck OK"
    }
}
