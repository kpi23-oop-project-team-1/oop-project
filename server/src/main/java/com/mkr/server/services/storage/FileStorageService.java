package com.mkr.server.services.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileStorageService implements StorageService {
    @NotNull
    private final Path dirPath;

    public FileStorageService(@NotNull Path dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public void store(@NotNull MultipartFile file, @Nullable String customFileName) {
        if (customFileName == null) {
            customFileName = file.getName();
        }

        try (InputStream stream = file.getInputStream()) {
            Path target = dirPath.resolve(customFileName).toAbsolutePath();

            Files.createDirectories(target.getParent());

            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public Path resolvePath(@NotNull String fileName) {
        return dirPath.resolve(fileName);
    }

    @Override
    @NotNull
    public Resource getFileResource(@NotNull String name) {
        Path target = dirPath.resolve(name);

        try {
            return new UrlResource(target.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
