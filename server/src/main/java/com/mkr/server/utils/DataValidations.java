package com.mkr.server.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class DataValidations {
    private static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private DataValidations() {
    }

    public static boolean isValidEmail(@NotNull String email) {
        return emailPattern.matcher(email).matches();
    }

    public static boolean isValidTelNumber(@NotNull String text) {
        if (text.length() != 10) {
            return false;
        }

        for (int i = 0; i  < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
