package com.vivy.shortener.util;

import com.vivy.shortener.util.exception.AssertionException;

import java.util.function.Function;
import java.util.function.Supplier;

public class AssertUtil {

    public static <E extends Throwable, T> void assertIsValid(T t, Function<T, Boolean> validator, Supplier<E> exception) throws E {
        if (!validator.apply(t)) {
            throw exception.get();
        }
    }

    public static <T> void assertIsValid(T t, Function<T, Boolean> validator, String exception) {
        assertIsValid(t, validator, () -> new AssertionException(exception));
    }

    public static <E extends Throwable> void assertIsPositive(Integer integer, Supplier<E> exception) throws E {
        assertIsValid(integer, i -> i > 0, exception);
    }

    public static void assertIsPositive(Integer integer, String exception) {
        assertIsPositive(integer, () -> new AssertionException(exception));
    }

    public static <E extends Throwable> void assertIsNotNegative(Integer integer, Supplier<E> exception) throws E {
        assertIsValid(integer, i -> i >= 0, exception);
    }

    public static <E extends Throwable> void assertIsNotNegative(Integer integer, String exception) {
        assertIsNotNegative(integer, () -> new AssertionException(exception));
    }

    public static <E extends Throwable> void assertValidUrl(String url, Supplier<E> exception) throws E {
        assertIsValid(url, ValidationUtil::isValidUrl, exception);
    }

    public static void assertValidUrl(String url, String exception) {
        assertValidUrl(url, () -> new AssertionException(exception));
    }

}
