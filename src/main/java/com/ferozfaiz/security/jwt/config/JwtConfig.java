package com.ferozfaiz.security.jwt.config;

/**
 * @author Feroz Faiz
 */

public class JwtConfig {
    private final String rsaPrivateKey;
    private final String rsaPublicKey;
    private final long tokenValidity;
    private final long refreshTokenValidity;
    private final String issuer;

    public JwtConfig(String rsaPrivateKey, String rsaPublicKey, long tokenValidity, long refreshTokenValidity, String issuer) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.rsaPublicKey = rsaPublicKey;
        this.tokenValidity = tokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.issuer = issuer;
    }

    public String getRsaPrivateKey() { return rsaPrivateKey; }
    public String getRsaPublicKey() { return rsaPublicKey; }
    public long getTokenValidity() { return tokenValidity; }
    public long getRefreshTokenValidity() { return refreshTokenValidity; }
    public String getIssuer() { return issuer; }
}
