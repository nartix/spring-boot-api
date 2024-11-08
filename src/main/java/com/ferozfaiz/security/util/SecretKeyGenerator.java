package com.ferozfaiz.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.SecretKey;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        SecretKey key = Jwts.SIG.HS256.key().build();
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Secret Key: " + secretString);
    }

    public static SecretKey generateKey() {
        return Jwts.SIG.HS256.key().build();
    }
}
