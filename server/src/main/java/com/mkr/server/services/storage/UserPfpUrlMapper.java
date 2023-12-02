package com.mkr.server.services.storage;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UserPfpUrlMapper {
    @NotNull
    String getEndpointUrl(int entityId);
}
