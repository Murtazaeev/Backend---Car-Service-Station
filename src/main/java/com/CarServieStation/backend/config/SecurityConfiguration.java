package com.CarServieStation.backend.config;



import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.CarServieStation.backend.entity.Permission.*;
import static com.CarServieStation.backend.entity.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
//            "/api/v1/demo-controller/**",
            "/api/v1/auth/**",
            "/dumb",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers("/api/v1/demo-controller/user").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())
                                .requestMatchers("/api/v1/demo-controller/employee").hasAnyRole(MANAGER.name(), ADMIN.name())
                                .requestMatchers("/api/v1/demo-controller/admin").hasAnyRole(ADMIN.name())
                                .requestMatchers("/api/v1/users").hasAnyRole(ADMIN.name())
                                .requestMatchers("/api/v1/users/**").hasAnyRole(ADMIN.name())
                                .requestMatchers(GET, "/api/v1/users/**").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers(PUT, "/api/v1/users/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(POST, "/api/v1/users").hasAnyAuthority(ADMIN_CREATE.name())
                                .requestMatchers(DELETE, "/api/v1/users/**").hasAnyAuthority(ADMIN_DELETE.name())
                                .requestMatchers("/api/v1/stations").hasAnyRole(ADMIN.name())
                                .requestMatchers("/api/v1/stations/**").hasAnyRole(ADMIN.name())
                                .requestMatchers(GET, "/api/v1/stations").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers(GET, "/api/v1/stations/**").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers(PUT, "/api/v1/stations/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(POST, "/api/v1/stations/**").hasAnyAuthority(ADMIN_CREATE.name())
                                .requestMatchers(DELETE, "/api/v1/stations/**").hasAnyAuthority(ADMIN_DELETE.name())
                                .requestMatchers(GET,"/api/v1/demo-controller/user").hasAnyAuthority(USER_READ.name(), MANAGER_READ.name(), ADMIN_READ.name())
                                .requestMatchers(GET,"/api/v1/demo-controller/employee").hasAnyAuthority(MANAGER_READ.name(), ADMIN_READ.name())
                                .requestMatchers(GET,"/api/v1/demo-controller/admin").hasAnyAuthority(ADMIN_READ.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // or use Collections.singletonList
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}