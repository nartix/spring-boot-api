package com.ferozfaiz.security.user;

import com.ferozfaiz.security.dto.UserRegistrationDto;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MessageSource messageSource;

    public UserService(
            UserRepository userRepository,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    public User createUser(UserRegistrationDto dto) throws MethodArgumentNotValidException, NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this, "userRegistrationDto");

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            bindingResult.addError(new FieldError(
                    "userRegistrationDto",    // objectName (same as your binding target)
                    "username",                         // field
                    dto.getUsername(),                  // rejectedValue
                    false,                              // bindingFailure — false for validation
                    null,                               // codes — you can pass specific codes if needed
                    null,                               // arguments
                    messageSource.getMessage(
                            "common.validation.constraints.UsernameExists.message",
                            null,
                            "Username already exists",
                            LocaleContextHolder.getLocale()
                    )));
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            bindingResult.addError(new FieldError(
                    "userRegistrationDto",    // objectName (same as your binding target)
                    "email",                            // field
                    dto.getEmail(),                     // rejectedValue
                    false,                              // bindingFailure — false for validation
                    null,                               // codes — you can pass specific codes if needed
                    null,                               // arguments
                    messageSource.getMessage(
                            "common.validation.constraints.EmailExists.message",
                            null,
                            "Email already exists",
                            LocaleContextHolder.getLocale()
                    )));
        }
        if (bindingResult.hasErrors()) {
            MethodParameter param = new MethodParameter(
                    this.getClass()
                            .getDeclaredMethod("createUser", UserRegistrationDto.class),
                    0 // index of the parameter that failed validation (username)
            );
            throw new MethodArgumentNotValidException(param, bindingResult);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
}

