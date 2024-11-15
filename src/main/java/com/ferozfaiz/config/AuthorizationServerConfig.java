package com.ferozfaiz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.with(authorizationServerConfigurer, Customizer.withDefaults());

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0

        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));



        http.authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

//        String encodedSecret = passwordEncoder.encode("client-secret");
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client-id")
                .clientSecret("{bcrypt}$2a$10$HbY00/u6PlgK1DWXw2yDR.jQ5HB/jbzMVu9AvoYdRQPLGWwivakou")
//                .clientSecret("{noop}password")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                .redirectUri("http://localhost:8080/login/oauth2/code/oidc-client")
                .scope("read")
                .scope("write")
                .scope("openid")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Value("${spring.security.oauth2.authorizationserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.authorizationserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            OAuth2TokenClaimsSet.Builder claims = context.getClaims();
            // Customize claims

        };
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getJwsHeader();
            JwtClaimsSet.Builder claims = context.getClaims();

            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {

                Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

                // Access the authentication object associated with the grant
                Authentication authentication = context.getPrincipal();

                if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                    claims.claim("username", userDetails.getUsername()); // Add username to claims
                } else if (context.getAuthorizationGrantType().equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
                    // For client_credentials, set client ID or other relevant info as needed
                    claims.claim("client_id", context.getRegisteredClient().getClientId());
                }

                // Add any additional custom claims as needed
                claims.claim("custom-claim", "custom-value");
            }
        };
    }

}

