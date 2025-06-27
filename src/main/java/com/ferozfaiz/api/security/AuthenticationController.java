package com.ferozfaiz.api.security;

import com.ferozfaiz.common.exception.exception.AuthenticationFailedException;
import com.ferozfaiz.security.dto.AuthenticationRequestDto;
import com.ferozfaiz.security.dto.UserRegistrationDto;
import com.ferozfaiz.security.jwt.dto.TokenResponseDto;
import com.ferozfaiz.security.jwt.service.JwtService;
import com.ferozfaiz.security.jwt.util.JwtUtil;
import com.ferozfaiz.security.user.User;
import com.ferozfaiz.security.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("${spring.data.rest.basePath}/auth/token")
    public ResponseEntity<TokenResponseDto> createAuthenticationToken(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto) throws Exception {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Incorrect username or password");
        }

        // Load user details and generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDto.getUsername());

        final TokenResponseDto tokens = jwtService.generateTokens(userDetails.getUsername());

        // Return the token
        return ResponseEntity.ok(tokens);
    }

    @CrossOrigin
    @PostMapping("${spring.data.rest.basePath}/auth/login")
    public ResponseEntity<UserDetails> authenticateUser(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto, HttpServletRequest request) throws Exception {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Incorrect username or password");
        }

        // Load user details and generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDto.getUsername());

//        final TokenResponseDto tokens = jwtService.generateTokens(userDetails.getUsername());

        // Create a session and set the user details as an attribute
//        HttpSession session = request.getSession(true);
//        session.setAttribute("user", userDetails);

        // Return the user details
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("${spring.data.rest.basePath}/auth/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) throws MethodArgumentNotValidException, NoSuchMethodException {
        User createdUser = userDetailsService.createUser(registrationDto);
        return ResponseEntity.status(201).body(createdUser);
//        return ResponseEntity.status(201).build();
    }

    @PostMapping("${spring.data.rest.basePath}/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(400).body("Missing refresh token");
        }

        // Check if token is a refresh token (optional based on your implementation)
        String tokenType = jwtService.getClaimFromToken(refreshToken, claims -> claims.get("token_type", String.class));
        if (tokenType == null || !tokenType.equals("refresh")) {
            return ResponseEntity.status(401).body("Invalid token type");
        }

        // Validate the refresh token
        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        // Extract the username from the refresh token
        String username = jwtService.getUsernameFromToken(refreshToken);

        // Generate a new access token
        String newAccessToken = jwtService.generateAccessToken(username);

        // Return the new access token in the response
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, jwtService.getJwtConfig().getTokenValidity(), refreshToken, jwtService.getJwtConfig().getRefreshTokenValidity()));
    }

    @GetMapping("${spring.data.rest.basePath}/auth/userinfo")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token); // Method to extract username from token
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        User user = userDetailsService.loadUserByUsername(username); // Fetch user info from DB
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok(user); // Return user details as JSON
    }

}