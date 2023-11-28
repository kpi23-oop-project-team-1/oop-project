package com.mkr.server.controllers;

import com.mkr.server.domain.UserRole;
import com.mkr.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/api/auth")
    public String auth() {
        return "";
    }

    @GetMapping("/api/usertype")
    public ResponseEntity<UserRole> userType(Authentication auth) {
        String email = auth.getName();

        return userRepo.findUserByEmail(email)
            .map(u -> ResponseEntity.ofNullable(u.getRole()))
            .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
