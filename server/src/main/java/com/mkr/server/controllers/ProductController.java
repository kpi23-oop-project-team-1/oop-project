package com.mkr.server.controllers;

import com.mkr.server.dto.PostCommentInfo;
import com.mkr.server.services.ProductService;
import com.mkr.server.services.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/postproductcomment")
    public void postProductComment(@Valid PostCommentInfo info, Authentication auth) {
        int userId = getUserId(auth);

        productService.addComment(info.targetId(), userId, info.rating(), info.text());
    }

    private int getUserId(@NotNull Authentication auth) {
        int userId = userService.getUserIdByEmail(auth.getName());
        if (userId < 0) {
            throw new UsernameNotFoundException("Username not found");
        }

        return userId;
    }
}
