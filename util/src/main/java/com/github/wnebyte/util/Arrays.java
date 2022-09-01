package com.github.wnebyte.util;

public class Arrays {

    public static int indexOf(Object[] array, Object element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static int[] fill(int startVal, int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = i + startVal;
        }
        return array;
    }
}
