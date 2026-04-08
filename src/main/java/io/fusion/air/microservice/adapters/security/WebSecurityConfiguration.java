/**
 * (C) Copyright 2021 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.adapters.security;

import io.fusion.air.microservice.server.config.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.csrf.CsrfToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.io.IOException;


/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    private ServiceConfiguration serviceConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String apiPath = serviceConfig.getApiDocPath();
        String hostName = serviceConfig.getServerHost();

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(apiPath + "/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/403")
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> {
                headers.frameOptions(frameOptions -> frameOptions.deny());
                headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; "
                        +"script-src 'self' *."+hostName+"; "
                        +"object-src 'self' *."+hostName+"; "
                        +"img-src 'self'; media-src 'self'; frame-src 'self'; font-src 'self'; connect-src 'self'"));
            });

        return http.build();
    }

    /**
     * WebSecurityCustomizer replaces the old configure(WebSecurity web) override.
     * Tells Spring Security to ignore static resources.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

    /**
     * Handles Malicious URI Path (handles special characters and other things)
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        return firewall;
    }

    /**
     * ONLY For Local Testing with Custom CSRF Headers in Swagger API Docs
     */
    private static class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            response.setHeader("X-CSRF-HEADER", token.getHeaderName());
            response.setHeader("X-CSRF-PARAM", token.getParameterName());
            response.setHeader("X-CSRF-TOKEN", token.getToken());
            filterChain.doFilter(request, response);
        }
    }
}

