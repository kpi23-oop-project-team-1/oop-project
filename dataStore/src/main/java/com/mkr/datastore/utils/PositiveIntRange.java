package com.mkr.datastore.utils;

import org.jetbrains.annotations.NotNull;

public record PositiveIntRange(int start, int length) {
    private static final PositiveIntRange FULL = new PositiveIntRange(0, Integer.MAX_VALUE);
    private static final PositiveIntRange EMPTY = new PositiveIntRange(0, 0);

    public PositiveIntRange {
        if (start < 0) {
            throw new IllegalArgumentException("start is negative");
        }

        if (length < 0) {
            throw new IllegalArgumentException("length is negative");
        }

        if (start + length < 0) {
            throw new IllegalArgumentException("The range can't be represented");
        }
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public int end() {
        return start + length;
    }

    @NotNull
    public PositiveIntRange intersectionRelative(@NotNull PositiveIntRange other) {
        int thisEnd = end();

        int otherStart = start + other.start;
        int otherEnd = otherStart + other.length;

        if (start <= otherEnd && otherStart <= thisEnd) {
            int resultStart = Math.max(start, otherStart);
            int resultEnd = Math.min(thisEnd, otherEnd);

            int resultLength = resultEnd - resultStart;

            return new PositiveIntRange(resultStart, resultLength);
        }

        return empty();
    }

    @NotNull
    public static PositiveIntRange full() {
        return FULL;
    }

    @NotNull
    public static PositiveIntRange empty() {
        return EMPTY;
    }
}
