package com.mkr.server.controllers;

import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.ProductCategory;
import com.mkr.server.domain.ProductState;
import com.mkr.server.dto.NewProductInfo;
import com.mkr.server.dto.PostCommentInfo;
import com.mkr.server.dto.ProductInfo;
import com.mkr.server.dto.UpdateProductInfo;
import com.mkr.server.services.ProductService;
import com.mkr.server.services.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/product")
    public ResponseEntity<ProductInfo> productInfo(int id) {
        return ResponseEntity.of(productService.getProductInfo(id));
    }

    @GetMapping("/images/product/{id}/{index}")
    public ResponseEntity<Resource> productImage(@PathVariable int id, @PathVariable int index) {
        Resource resource = productService.getImageResource(id, index);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\""
        ).body(resource);
    }

    @PostMapping("/api/addproduct")
    public void postAddProduct(@Valid NewProductInfo form, Authentication auth) {
        productService.addProduct(
                getUserId(auth),
                form.title(),
                form.price(),
                form.amount(),
                ProductCategory.forValue(form.category()),
                ProductState.forValue(form.state()),
                ColorId.forValue(form.color()),
                form.image()
        );
    }

    @PostMapping("/api/updateproduct")
    public void postUpdateProduct(@Valid UpdateProductInfo form, Authentication auth) {
        if (!productService.checkIfTraderMatches(form.id(), getUserId(auth))) return;

        productService.updateProduct(
                form.id(),
                form.title(),
                form.price(),
                form.amount(),
                ProductCategory.forValue(form.category()),
                ProductState.forValue(form.state()),
                ColorId.forValue(form.color()),
                form.image()
        );
    }

    @DeleteMapping("/api/deleteproducts")
    public void deleteProduct(@RequestBody String idsJson, Authentication auth) {
        String[] strings = idsJson.substring(1, idsJson.length() - 1).split(",");

        int[] ids = new int[strings.length];
        Arrays.setAll(ids, i -> Integer.parseInt(strings[i]));

        for (var id : ids) {
            if (!productService.checkIfTraderMatches(id, getUserId(auth))) continue;
            productService.deleteProduct(id);
        }
    }

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
