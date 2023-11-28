package com.mkr.server.controllers;

import com.mkr.server.domain.UserRole;
import com.mkr.server.dto.SignUpForm;
import com.mkr.server.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/auth")
    public String auth() {
        return "";
    }

    @GetMapping("/api/usertype")
    public ResponseEntity<UserRole> userType(Authentication auth) {
        String email = auth.getName();

        return userService.findUserByEmail(email)
            .map(u -> ResponseEntity.ofNullable(u.getRole()))
            .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/api/signup")
    public ResponseEntity<Object> signUp(@ModelAttribute @Valid SignUpForm form) {
        userService.addCustomerTraderUser(
            form.getEmail(),
            form.getPassword(),
            form.getFirstName(),
            form.getLastName(),
            form.getTelNumber()
        );

        return ResponseEntity.ok().build();
    }
}
