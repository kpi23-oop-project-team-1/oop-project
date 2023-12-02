package com.mkr.server.dto;

import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Nullable;

public record UpdateAccountInfo(
    @Nullable String password,
    @NotEmpty String username,
    @NotEmpty String aboutMe,
    @NotEmpty String firstName,
    @NotEmpty String lastName,
    @NotEmpty String telNumber
) {
}
