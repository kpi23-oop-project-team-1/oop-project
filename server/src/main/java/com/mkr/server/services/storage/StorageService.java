package com.mkr.server.services.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void store(@NotNull MultipartFile file, @Nullable String customFileName);

    @NotNull
    Path resolvePath(@NotNull String fileName);

    @NotNull
    Resource getFileResource(@NotNull String name);
}
