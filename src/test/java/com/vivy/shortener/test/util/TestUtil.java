package com.vivy.shortener.test.util;

public class TestUtil {

    public static String createBigString(char c, int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(c);
        }
        return builder.toString();
    }
}
