package com.mkr.server.config;

import com.mkr.server.services.storage.ProductImageUrlMapper;
import com.mkr.server.services.storage.UserPfpUrlMapper;
import com.mkr.server.services.storage.FileStorageService;
import com.mkr.server.services.storage.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileStorageConfig {
    @Bean
    public StorageService userPfpStorageService() {
        return new FileStorageService(Path.of("images", "user_pfp"));
    }

    @Bean
    public StorageService productImageStorageService() {
        return new FileStorageService(Path.of("images", "product_image"));
    }

    @Bean
    public UserPfpUrlMapper userPfpUrlMapper() {
        return entityId -> "/images/pfp/" + entityId;
    }
    
    @Bean
    public ProductImageUrlMapper productImageUrlMapper() {
        return (entityId, index) -> "/images/product/" + entityId + "/" + index;
    }
}
