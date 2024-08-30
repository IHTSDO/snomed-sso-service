package org.snomed.ssoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration {
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String ROLE = "USER";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String SWAGGER_API_DOCS = "/v3/api-docs/**";

    private static final String[] SWAGGER_ANONLIST = {
            "/api/reset_password", "/api/forgot_password"
    };

    private static final String[] SWAGGER_PERMITLIST = {
            SWAGGER_API_DOCS, SWAGGER_UI, "/health",
            "/api/authenticate", "/api/account", "/api/account/logout", "/api/cache/**"
    };

    private static final String[] SWAGGER_AUTHLIST = {
            "/api/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(SWAGGER_ANONLIST).anonymous()
                        .requestMatchers(SWAGGER_PERMITLIST).permitAll()
                        .requestMatchers(SWAGGER_AUTHLIST).authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails userDetails = User.withUsername(USER)
                .password(passwordEncoder.encode(PASSWORD))
                .roles(ROLE)
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

