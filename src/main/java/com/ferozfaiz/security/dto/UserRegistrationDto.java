package com.ferozfaiz.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author Feroz Faiz
 */
public class UserRegistrationDto {
    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(
            regexp = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$",
            message = "{common.validation.constraints.Pattern.ValidUsername.message}"
    )
    private String username;

    @NotBlank(message = "{common.validation.constraints.NotBlank.message}")
    @Size(min = 8, max = 128, message = "{common.validation.constraints.Size.message}")
    private String password;

    @Email
    @NotBlank
    private String email;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
