package com.vivy.shortener.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {

    private final static Random RANDOM = new SecureRandom();

    private final static char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static String randomAlphaNumericString(int len) {
        return RANDOM.ints(len, 0, CHARS.length)
                .mapToObj(i -> CHARS[i])
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}