package com.vsmoon.editor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextUtils {

    public static int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    public static int countCharacters(String text) {
        if (text == null) {
            return 0;
        }
        return text.length();
    }

    public static String toUpperCase(String text) {
        return text == null ? "" : text.toUpperCase();
    }

    public static String toLowerCase(String text) {
        return text == null ? "" : text.toLowerCase();
    }

    public static String getCurrentTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return dtf.format(LocalDateTime.now());
    }
}