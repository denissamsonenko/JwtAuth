package com.company.security.controller;

import com.company.security.exception.ResourceNotFoundException;
import com.company.security.model.User;
import com.company.security.repository.UserRepository;
import com.company.security.sec.CurrentUser;
import com.company.security.sec.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
