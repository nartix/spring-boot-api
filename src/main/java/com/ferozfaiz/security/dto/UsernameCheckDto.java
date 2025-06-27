package com.ferozfaiz.security.dto;

import com.ferozfaiz.common.annotation.ValidUsername;

/**
 * @author Feroz Faiz
 */
public class UsernameCheckDto {

    @ValidUsername
    private String username;

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
