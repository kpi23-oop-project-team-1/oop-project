package com.mkr.server.controllers;

import com.mkr.server.domain.ProductStatus;
import com.mkr.server.dto.ConciseProduct;
import com.mkr.server.search.UserProductSearchDescription;
import com.mkr.server.services.UserProductSearchService;
import com.mkr.server.services.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProductSearchController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProductSearchService searchService;

    @GetMapping("/api/userproductssearchdesc")
    public UserProductSearchDescription userProductsSearchDescription(
        @RequestParam("status") ProductStatus status,
        Authentication auth
    ) {
        return searchService.getDescription(status, getUserId(auth));
    }

    @GetMapping("/api/userproducts")
    public ConciseProduct[] userProducts(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam("status") ProductStatus status,
        Authentication auth
    ) {
        return searchService.getUserProducts(
            status,
            page == null ? 1 : page,
            getUserId(auth)
        );
    }

    private int getUserId(@NotNull Authentication auth) {
        int userId = userService.getUserIdByEmail(auth.getName());
        if (userId < 0) {
            throw new UsernameNotFoundException("Username not found");
        }

        return userId;
    }
}
