package com.ferozfaiz.controller;

import com.ferozfaiz.common.util.DateTimeUtil;
import com.ferozfaiz.security.util.SecretKeyGenerator;
import io.jsonwebtoken.io.Encoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.crypto.SecretKey;


@RestController
public class HelloController {

//    @Value("${TEST}")
//    private String secret;

//    @Value("${API_BASE_URL}")
//    private String secret;

    @Autowired
    private DateTimeUtil dateTimeUtil;

//    @PreAuthorize("hasRole('ROLE_MOD')")
//    @PreAuthorize("hasAuthority('SCOPE_read')")
    @GetMapping("/hello")
    public HelloWorld hello() {
        return new HelloWorld("Hello, World! feroz!aa");
    }

//    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/hello2")
    @ResponseBody
    public String hello2( Authentication authentication, WebRequest request) throws NoResourceFoundException {

        String username = (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken))
                ? authentication.getName()
                : "Anonymous";


        SecretKey key = SecretKeyGenerator.generateKey();
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        throw new NoResourceFoundException(HttpMethod.GET, request.getDescription(false));

//        return "Hello, " + username + "! " + secretString + dateTimeUtil.getCurrentOffsetDateTime();
    }

//    @Value("${JWT_SECRET}")
//    private String jwtSecret ;
//
//    public JwtEncoder jwtEncoder() {
//        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
//        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey)
//                .keyUse(KeyUse.SIGNATURE)
//                .keyID(UUID.randomUUID().toString())
//                .algorithm(JWSAlgorithm.HS256)
//                .build();
//        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
//        return new NimbusJwtEncoder(jwkSource);
//    }

//    @GetMapping("/")
//    public String home() {
////        JWKSource<Securitycontext> jwkSource = null;
////        NimbusJwtEncoder encoder = new NimbusJwtEncoder(JWKSource<Securitycontext> jwkSource);
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .subject("feroz")
//                .issuer("https://ferozfaiz.com")
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusSeconds(3600))
//                .build();
//        JwsHeader header = JwsHeader.with(() -> "HS256").build();
//        Jwt jwt = jwtEncoder().encode(JwtEncoderParameters.from(header, claims));
////        return jwt.getTokenValue();
//        return "Welcome to the home page! " + jwt.getTokenValue();
//    }
    @GetMapping("/login/oauth2/code/oidc-client")
    public String handleAuthorizationCode(@RequestParam("code") String code) {
        // Process the authorization code
        return "Authorization code received: " + code;
    }


    @Autowired
    private AuthenticationManager authenticationManager;

//    @Autowired
//    private JwtEncoder jwtEncoder;
//
//    @PostMapping("/api/authenticate")
//    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            String token = generateToken(authentication);
//            return ResponseEntity.ok(new AuthenticationResponse(token));
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
//
//    private String generateToken(Authentication authentication) {
//        Instant now = Instant.now();
//        long expiry = 3600L; // 1 hour
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(now)
//                .expiresAt(now.plusSeconds(expiry))
//                .subject(authentication.getName())
//                .claim("scope", "read")
//                .build();
//
//        JwsHeader header = JwsHeader.with(() -> "HS256").build();
//        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
//    }
//
//    @Autowired
//    private OAuth2TokenGenerator<?> tokenGenerator;
//
//    @Autowired
//    private RegisteredClientRepository registeredClientRepository;
//
//    @PostMapping("/api/authenticate")
//    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            OAuth2AccessToken accessToken = generateToken(authentication);
//            return ResponseEntity.ok(accessToken);
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
//
//    @Autowired
//    OAuth2AuthorizationService authorizationService;
//
//    private OAuth2AccessToken   generateToken(Authentication authentication) {
//        RegisteredClient registeredClient = registeredClientRepository.findByClientId("client-id");
//        if (registeredClient == null) {
//            throw new IllegalArgumentException("Client not found");
//        }
//
//        Set<String> authorizedScopes = Collections.singleton("read");
//
//        AuthorizationServerSettings authorizationServerSettings = AuthorizationServerSettings.builder().build();
//
//        AuthorizationServerContext authorizationServerContext = new DefaultAuthorizationServerContext(
//                authorizationServerSettings.getIssuer(),
//                authorizationServerSettings
//        );
//        try {
//        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
//                .registeredClient(registeredClient)
//                .principal(authentication)
//                .authorizationServerContext(authorizationServerContext)
//                .authorizedScopes(registeredClient.getScopes())
//                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                .build();
//
//        // Generate the access token
//            OAuth2Token accessToken =  tokenGenerator.generate(tokenContext);
//        if (accessToken == null) {
//            throw new IllegalStateException("Token generation failed");
//        }
//
////        return accessToken;
//
//            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
//                    .principalName(registeredClient.getClientId())
//                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                    .authorizedScopes(authorizedScopes);
//
//            OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken.getTokenValue(),
//                    accessToken.getIssuedAt(), accessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
//
//            OAuth2TokenFormat accessTokenFormat = tokenContext.getRegisteredClient()
//                    .getTokenSettings()
//                    .getAccessTokenFormat();
//
//            authorizationBuilder.token(token, (metadata) -> {
//                if (accessToken instanceof ClaimAccessor claimAccessor) {
//                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims());
//                }
//                metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
//                metadata.put(OAuth2TokenFormat.class.getName(), accessTokenFormat.getValue());
//            });
//
//            authorizationBuilder.accessToken(token);
//            OAuth2Authorization authorization = authorizationBuilder.build();
//            authorizationService.save(authorization);
//
//            return token;
////            return accessToken;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
}