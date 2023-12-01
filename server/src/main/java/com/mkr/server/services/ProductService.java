package com.mkr.server.services;

import com.mkr.server.domain.Comment;
import com.mkr.server.repositories.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public void addComment(int productId, int userId, int rating, @NotNull String text) {
        long postEpochSeconds = LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC);

        var comment = new Comment(0, productId, userId, rating, text, postEpochSeconds);
        repo.addComment(comment);
    }
}
