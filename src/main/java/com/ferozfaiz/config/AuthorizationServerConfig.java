package com.ferozfaiz.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
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
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new
                OAuth2AuthorizationServerConfigurer();

        http.csrf(AbstractHttpConfigurer::disable);

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
                )
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    // old working
//    @Bean
//    @Order(1)
//    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
//            throws Exception {
//
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
//        http.with(authorizationServerConfigurer, Customizer.withDefaults());
//
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//
//        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
//                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
//
//        http
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling((exceptions) -> exceptions
//                        .defaultAuthenticationEntryPointFor(
//                                new LoginUrlAuthenticationEntryPoint("/login"),
//                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                        )
//                )
//                // Accept access tokens for User Info and/or Client Registration
//                .oauth2ResourceServer((resourceServer) -> resourceServer
//                        .jwt(Customizer.withDefaults()));
//
//
//
//        http.authenticationProvider(authenticationProvider);
//
//        return http.build();
//    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${NEXTJS_OIDC_CLIENT_ID}")
    private String clientId;

    @Value("${NEXTJS_OIDC_CLIENT_SECRET_ENCODED}")
    private String clientSecret;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
//        String encodedSecret = passwordEncoder.encode("u6PlgK1DWXw2yDR");
//        System.out.println("encodedSecret: " + encodedSecret);
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(clientSecret)
//                .clientSecret("{noop}password")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8080/login/oauth2/code/oidc-client")
                .scope("read")
                .scope("write")
                .scope("openid")
                .scope("profile")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

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

    @Value("${RSA_KEY_PRIVATE_BASE64_ENCODED}")
    private String rsaPrivateKeyBase64;

    @Value("${RSA_KEY_PUBLIC_BASE64_ENCODED}")
    private String rsaPublicKeyBase64;

    @Value("${RSA_KEY_ID}")
    private String rsaKeyId;

    private KeyPair loadRsaKeyPair() {
        try {
            // Decode Base64-encoded keys
            byte[] privateKeyBytes = Base64.getDecoder().decode(rsaPrivateKeyBase64);
            byte[] publicKeyBytes = Base64.getDecoder().decode(rsaPublicKeyBase64);

            // Generate RSAPrivateKey from decoded bytes
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            // Generate RSAPublicKey from decoded bytes
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

            return new KeyPair(publicKey, privateKey);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load RSA keys", ex);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = loadRsaKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
//                .keyID(UUID.randomUUID().toString()) // Optionally set a specific key ID
                .keyID(rsaKeyId)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}

