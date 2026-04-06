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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.csrf.CsrfFilter;
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
        // Forces All Request to be Secured (HTTPS)
        // http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        String apiPath = serviceConfig.getApiDocPath();
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(apiPath + "/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                // This configures exception handling, specifically specifying that when a user tries to access a page
                // they're not authorized to view, they're redirected to "/403" (typically an "Access Denied" page).
                .exceptionHandling(exceptions -> exceptions.accessDeniedPage("/403"));
        // Enable CSRF Protection
        // This line configures the Cross-Site Request Forgery (CSRF) protection, using a Cookie-based CSRF token
        // repository. This means that CSRF tokens will be stored in cookies. The withHttpOnlyFalse() method makes
        // these cookies accessible to client-side scripting, which is typically necessary for applications that use
        // a JavaScript-based frontend.
       /**
        http
                .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                // Add the above Only for testing in Swagger
                .addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
        */
        // Disabled for Local Testing
        http.csrf(csrf -> csrf.disable());
        // X-Frame-Options is a security header that is intended to protect your website against "clickjacking" attacks.
        http.headers(headers -> headers.frameOptions(frame -> frame.deny()));
        // Only for Local Testing
        // http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        String hostName = serviceConfig.getServerHost();
        // Content Security Policy
        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; "
                        +"script-src 'self' *."+hostName+"; "
                        +"object-src 'self' *."+hostName+"; "
                        +"img-src 'self'; media-src 'self'; frame-src 'self'; font-src 'self'; connect-src 'self'")));
        return http.build();
    }

    /**
     * The web.ignoring().requestMatchers(...) part of the code tells Spring Security to ignore the specified patterns and
     * not apply security to requests matching those. This is useful for static resources like CSS files, JavaScript
     * files, and images, which don't need to be secured.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

    /**
     * Handles Malicious URI Path (handles special characters and other things
     * @return
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        return firewall;
    }

    /**
     * ONLY For Local Testing with Custom CSRF Headers in Swagger APi Docs
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

