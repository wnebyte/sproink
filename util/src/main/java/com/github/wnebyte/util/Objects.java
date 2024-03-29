package com.github.wnebyte.util;

import java.util.function.Supplier;

public class Objects {

    public static <T> T requireNonNullElseGet(T value, Supplier<T> supplier) {
        return (value == null) ? supplier.get() : value;
    }
}
