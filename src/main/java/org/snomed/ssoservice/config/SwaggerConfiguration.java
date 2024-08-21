package org.snomed.ssoservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class SwaggerConfiguration {
    @Value("${swagger.title:SNOMED International SSO Service}")
    private String apiTitle;

    @Value("${swagger.description:SNOMED International ID & SSO Service}")
    private String apiDescription;

    @Value("${swagger.version:2.0}")
    private String apiVersion;

    @Value("${swagger.license:Apache 2.0}")
    private String licenseName;

    @Value("${swagger.licenseUrl:https//www.apache.org/licenses/LICENSE-2.0.html}")
    private String licenseUrl;

    @Value("${swagger.contact:support@ihtsdo.org}")
    private String contactEmail;

    @Bean
    public OpenAPI config() {
        return new OpenAPI().info(
                new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact().email(contactEmail))
                        .license(new License()
                                .name(licenseName)
                                .url(licenseUrl)
                        )
        );
    }
}
