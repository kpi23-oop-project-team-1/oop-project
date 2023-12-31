package com.mkr.server.controllers;

import com.mkr.server.domain.UserRole;
import com.mkr.server.dto.*;
import com.mkr.server.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/api/detaileduserinfo")
    public ResponseEntity<DetailedUserInfo> detailedUserInfo(int id) {
        return ResponseEntity.of(userService.getDetailedUserInfo(id));
    }

    @PostMapping("/api/postusercomment")
    public void postUserComment(NewCommentInfo commentInfo, Authentication auth) {
        String email = auth.getName();

        userService.addNewComment(email, commentInfo);
    }

    @GetMapping("/api/accountid")
    public int accountId(@RequestParam("email") String email) {
        return userService.getUserId(email);
    }

    @GetMapping("/api/accountinfo")
    public ResponseEntity<AccountInfo> accountInfo(@RequestParam("id") int id) {
        return ResponseEntity.of(userService.getAccountInfo(id));
    }

    @PostMapping("/api/updateaccountinfo")
    public void updateAccountInfo(
        @ModelAttribute UpdateAccountInfo info,
        @RequestParam(value = "pfpFile", required = false) MultipartFile pfpFile,
        Authentication auth
    ) {
        userService.updateAccountInfo(auth.getName(), info, pfpFile);
    }

    @PostMapping("/api/checkout")
    public void checkout(Authentication auth) {
        userService.checkout(auth.getName());
    }

    @GetMapping("/images/pfp/{id}")
    public ResponseEntity<Resource> pfpImage(@PathVariable int id) {
        Resource resource = userService.getImageResource(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\""
        ).body(resource);
    }
}
