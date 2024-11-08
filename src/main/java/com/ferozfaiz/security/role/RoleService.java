package com.ferozfaiz.security.role;

import com.ferozfaiz.security.user.User;
import com.ferozfaiz.security.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final UserRepository userRepository;

    public RoleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

public void assignRoleToUser(String username, Role role) {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        user.getRoles().add(role);
        userRepository.save(user);
    } else {
        // Handle the case where the user is not found
        throw new UsernameNotFoundException("security.exception.UserNotFoundException.username.message} " + username);
    }
}

public void removeRoleFromUser(String username, Role role) {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        user.getRoles().remove(role);
        userRepository.save(user);
    } else {
        // Handle the case where the user is not found
        throw new UsernameNotFoundException("{security.exception.UserNotFoundException.username.message} " + username);
    }
}
}
