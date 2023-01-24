package com.reactlibraryproject.springbootlibrary.Config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /* Disable Cross Site Request Forgery*/
        http.csrf().disable();

        /* Protect endpoint at /api/secure */
        http.authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers("/api/books/secure/**")
                                .authenticated()
                ).oauth2ResourceServer()
                .jwt();

        /* Add CORS filters */
        http.cors();

        /* Add content negotiation strategy */
        http.setSharedObject(
                ContentNegotiationStrategy.class
                ,new HeaderContentNegotiationStrategy());

        /* Force a non-empty response body for 401's to make the response friendly */
        Okta.configureResourceServer401ResponseBody(http);

        return http.build();
    }
}
