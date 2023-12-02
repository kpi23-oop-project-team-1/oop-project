package com.mkr.server.services;

import com.mkr.server.domain.*;
import com.mkr.server.dto.CommentInfo;
import com.mkr.server.dto.ProductInfo;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.services.storage.ProductImageUrlMapper;
import com.mkr.server.services.storage.StorageService;
import jakarta.validation.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("productImageStorageService")
    private StorageService storageService;

    @Autowired
    private ProductImageUrlMapper productImageUrlMapper;

    public Optional<ProductInfo> getProductInfo(int id) {
        return repo.findFirstProductBy(u -> u.getProductId() == id)
                .map(this::getProductInfo);
    }

    public void addProduct(
            int traderId,
            @NotNull String title,
            int price,
            int amount,
            @NotNull ProductCategory category,
            @NotNull ProductState state,
            @NotNull ColorId color,
            @NotNull MultipartFile[] imageFiles
    ) {
        if (imageFiles.length == 0) {
            throw new ValidationException("No images");
        }

        if (title.isEmpty()) {
            throw new ValidationException("Empty title");
        }

        if (price <= 0) {
            throw new ValidationException("Not positive price");
        }

        if (amount <= 0) {
            throw new ValidationException("Not positive amount");
        }

        int productId = repo.getLastID() + 1;

        // Save images
        for (int i = 0; i < imageFiles.length; i++) {
            MultipartFile file = imageFiles[i];
            storageService.store(file, getImageFileName(productId, i));
        }

        // Add product
        var product = new Product(
                repo.getLastID() + 1,
                traderId,
                title,
                price,
                amount,
                category,
                state,
                ProductStatus.WAITING_FOR_MODERATION,
                color
        );
        product.setComments(new Comment[0]);

        repo.addProduct(product);
    }

    public void updateProduct(
            int productId,
            @NotNull String title,
            int price,
            int amount,
            @NotNull ProductCategory category,
            @NotNull ProductState state,
            @NotNull ColorId color,
            @NotNull MultipartFile[] imageFiles
    ) {
        if (imageFiles.length == 0) {
            throw new ValidationException("No images");
        }

        if (title.isEmpty()) {
            throw new ValidationException("Empty title");
        }

        if (price <= 0) {
            throw new ValidationException("Not positive price");
        }

        if (amount <= 0) {
            throw new ValidationException("Not positive amount");
        }

        // Update images
        deleteImages(productId);

        for (int i = 0; i < imageFiles.length; i++) {
            MultipartFile file = imageFiles[i];
            storageService.store(file, getImageFileName(productId, i));
        }

        // Update product
        repo.updateProduct(
                productId,
                o -> {
                    var product = new Product(
                            o.getProductId(),
                            o.getTraderId(),
                            title,
                            price,
                            amount,
                            category,
                            state,
                            o.getStatus(),
                            color
                    );
                    product.setComments(o.getComments());
                    return product;
                }
        );
    }

    public void deleteProduct(int productId) {
        repo.deleteProduct(productId);

        deleteImages(productId);
    }

    public CommentInfo[] getProductComments(@NotNull Product product) {
        return Arrays.stream(product.getComments())
                .map(c -> new CommentInfo(
                        c.getId(),
                        userService.getConciseUserInfo(c.getUserId()).get(),
                        c.getRating(),
                        c.getText(),
                        LocalDateTime.ofEpochSecond(c.getPostEpochSeconds(), 0, ZoneOffset.UTC))
                )
                .toArray(CommentInfo[]::new);
    }

    public void addComment(int productId, int userId, int rating, @NotNull String text) {
        long postEpochSeconds = LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC);

        var comment = new Comment(0, productId, userId, rating, text, postEpochSeconds);
        repo.addComment(comment);
    }

    public boolean checkIfTraderMatches(int productId, int traderId) {
        Product product = repo.findFirstProductBy(u -> u.getProductId() == productId).get();

        return product.getTraderId() == traderId;
    }

    @NotNull
    private ProductInfo getProductInfo(@NotNull Product product) {
        int productId = product.getProductId();

        var imagePaths = getImagePaths(product.getProductId());
        var imageSources = new String[imagePaths.size()];
        Arrays.setAll(imageSources, i -> productImageUrlMapper.getEndpointUrl(productId, i));

        return new ProductInfo(
                productId,
                product.getTitle(),
                imageSources,
                "",
                product.getPrice(),
                product.getAmount(),
                product.getDescription(),
                getProductComments(product),
                product.getCategory().toPrettyString(),
                product.getState().toPrettyString(),
                product.getColor().toPrettyString()
        );
    }

    @Nullable
    public Resource getImageResource(int id, int index) {
        Resource resource = storageService.getFileResource(getImageFileName(id, index));

        if (resource.exists() && resource.isReadable()) {
            return resource;
        }

        return null;
    }

    private void deleteImages(int productId) {
        var imagePaths = getImagePaths(productId);

        for (var path : imagePaths) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ArrayList<Path> getImagePaths(int productId) {
        var imagePaths = new ArrayList<Path>();

        int index = 0;
        while (true) {
            var path = storageService.resolvePath(getImageFileName(productId, index));

            if (!path.toFile().exists()) break;

            imagePaths.add(path);

            index++;
        }

        return imagePaths;
    }

    private String getImageFileName(int id, int index) {
        return id + "_" + index + ".png";
    }
}
