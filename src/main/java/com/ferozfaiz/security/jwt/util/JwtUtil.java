package com.ferozfaiz.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Inject the secret key from application properties
//    @Value("${jwt.secret}")
    //use cloud vault that uses hashicorp vault
    @Value("${JWT_SECRET}")
    private String secret;

    // JWT token validity (e.g., 10 hours)
    @Value("${JWT_TOKEN_VALIDITY}")
    private long tokenValidity;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Decode the Base64-encoded secret key
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        // Create the SecretKey instance
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate token for a user
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("ferozfaiz.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(key)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException ex) {
            // Log the exception or handle it as per your requirement
            return false;
        }
    }

    // Get username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Get expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Retrieve a specific claim from token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    // Parse the token and retrieve claims
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
