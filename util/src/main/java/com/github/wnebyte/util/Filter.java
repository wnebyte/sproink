package com.github.wnebyte.util;

@FunctionalInterface
public interface Filter<T> {

    boolean pass(T t);
}
