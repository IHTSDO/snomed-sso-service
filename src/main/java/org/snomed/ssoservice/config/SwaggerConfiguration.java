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
    @Value("${swagger.title}")
    private String apiTitle;

    @Value("${swagger.description}")
    private String apiDescription;

    @Value("${swagger.version}")
    private String apiVersion;

    @Value("${swagger.license}")
    private String licenseName;

    @Value("${swagger.licenseUrl}")
    private String licenseUrl;

    @Value("${swagger.contact}")
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
