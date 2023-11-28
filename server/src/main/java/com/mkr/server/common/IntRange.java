package com.mkr.server.common;

public record IntRange(int start, int end) {
    public IntRange {
        if (start > end) {
            throw new IllegalArgumentException("start > end");
        }
    }
}
