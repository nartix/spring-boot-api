package com.ferozfaiz.config;

import com.ferozfaiz.security.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig  {

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.authorizationserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.data.rest.basePath}")
    private String apiBasePath;

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt"; // Default encoding algorithm
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());

        // Create the DelegatingPasswordEncoder
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userService);

        return provider;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.csrf(AbstractHttpConfigurer::disable);

        http
                .securityMatcher(apiBasePath + "/**")
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers(HttpMethod.GET, apiBasePath + "/sessions/search/deleteBySessionId").hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.PUT, apiBasePath + "/**").hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.PATCH, apiBasePath + "/**").hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.DELETE, apiBasePath + "/**").hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.POST, apiBasePath + "/**").hasAuthority("SCOPE_write")
                    .anyRequest().permitAll()
        );

        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt((jwt) -> jwt.jwkSetUri(jwkSetUri))
                .jwt(Customizer.withDefaults()));

        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.csrf(AbstractHttpConfigurer::disable);

        http
                .securityMatcher("/**")
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                .anyRequest().permitAll()
        );

        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());

//        http.oauth2ResourceServer((oauth2) -> oauth2
//                .jwt((jwt) -> jwt.jwkSetUri(jwkSetUri))
//                .jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable);
    }

}