package com.ferozfaiz.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("api/auth/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}