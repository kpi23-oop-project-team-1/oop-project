package com.mkr.server.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record IntRange(int start, int end) {
    public IntRange {
        if (start > end) {
            throw new IllegalArgumentException("start > end");
        }
    }

    public int length() {
        return end - start + 1;
    }

    public boolean contains(int value) {
        return value >= start && value <= end;
    }

    @NotNull
    public IntRange widen(int value) {
        int newStart = Math.min(start, value);
        int newEnd = Math.max(end, value);

        return new IntRange(newStart, newEnd);
    }

    @NotNull
    public IntRange union(@NotNull IntRange other) {
        return new IntRange(Math.min(start, other.start), Math.max(end, other.end));
    }

    @Nullable
    public static IntRange union(@Nullable IntRange a, IntRange b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }

        return a.union(b);
    }

    @NotNull
    public static IntRange fixedStep(int index, int step) {
        return new IntRange(index * step, (index + 1) * step);
    }

    @NotNull
    public static IntRange parse(@NotNull String text) {
        int dashIndex = text.indexOf('-');
        if (dashIndex < 0) {
            throw new IllegalArgumentException("text");
        }

        int start = Integer.parseInt(text.substring(0, dashIndex));
        int end = Integer.parseInt(text.substring(dashIndex + 1));

        return new IntRange(start, end);
    }
}
