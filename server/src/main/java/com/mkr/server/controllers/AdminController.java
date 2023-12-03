package com.mkr.server.controllers;

import com.mkr.server.domain.ProductStatus;
import com.mkr.server.dto.ConciseProduct;
import com.mkr.server.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @Autowired
    private ProductService productService;

    @PostMapping("/api/changeproductstatus")
    public void changeProductStatus(@RequestParam("id") int id, @RequestParam("status") ProductStatus newStatus) {
        productService.changeProductStatus(id, newStatus);
    }

    @GetMapping("/api/getproductswaitingapproval")
    public ConciseProduct[] getProductsWaitingApproval() {
        return productService.getProductsWaitingApproval();
    }
}
