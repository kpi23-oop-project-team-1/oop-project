package com.mkr.datastore;

import org.jetbrains.annotations.Nullable;

public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException() {
    }

    public InvalidEntityException(@Nullable String message) {
        super(message);
    }

    public InvalidEntityException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
