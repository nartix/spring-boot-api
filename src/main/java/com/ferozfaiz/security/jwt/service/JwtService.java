package com.ferozfaiz.security.jwt.service;

import com.ferozfaiz.security.jwt.config.JwtConfig;
import com.ferozfaiz.security.jwt.dto.TokenResponseDto;
import com.ferozfaiz.security.jwt.util.KeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * \@Service class to generate JWT tokens using RSA private key.
 *
 * This service class is responsible for generating JWT tokens for a given username.
 * It uses an RSA private key to sign the tokens.
 *
 * \@author Feroz Faiz
 */
@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.privateKey = KeyUtil.getPrivateKey(jwtConfig.getRsaPrivateKey());
            this.publicKey = KeyUtil.getPublicKey(jwtConfig.getRsaPublicKey());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JWT Service", e);
        }
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("https://" + jwtConfig.getIssuer())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getTokenValidity()))
                .signWith(privateKey)
                .id(UUID.randomUUID().toString())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("https://" + jwtConfig.getIssuer())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenValidity()))
                .signWith(privateKey)
                .claim("token_type", "refresh1")
                .id(UUID.randomUUID().toString())
                .compact();
    }

    public TokenResponseDto generateTokens(String username) {
        String accessToken = generateAccessToken(username);
        String refreshToken = generateRefreshToken(username);
        return new TokenResponseDto(accessToken, jwtConfig.getTokenValidity(), refreshToken, jwtConfig.getRefreshTokenValidity());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Token is invalid or expired
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Retrieve a specific claim from token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtConfig getJwtConfig() {
        return jwtConfig;
    }
}
