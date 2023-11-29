package com.mkr.server.services;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.ProductStatus;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.search.UserProductSearchDescription;
import com.mkr.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProductSearchService {
    private static final int PAGE_SIZE = 20;

    @Autowired
    private ProductRepository repo;

    @NotNull
    public UserProductSearchDescription getDescription(@NotNull ProductStatus status, int userId) {
        int totalCount = repo.countUserProducts(p -> p.getStatus() == status, userId);
        int pageCount = MathUtils.ceilDiv(totalCount, PAGE_SIZE);

        return new UserProductSearchDescription(pageCount);
    }

    @NotNull
    public Product[] getUserProducts(@NotNull ProductStatus status, int page, int userId) {
        var range = IntRange.fixedStep(page - 1, PAGE_SIZE);

        return repo.getUserProducts(p -> p.getStatus() == status, range, userId);
    }
}
