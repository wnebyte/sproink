package com.github.wnebyte.util;

import java.util.List;
import java.util.ArrayList;

public class Lists {

    public static List<Character> of(char[] chars) {
        if (chars == null) return null;
        List<Character> c = new ArrayList<>(chars.length);
        for (int i = 0; i < chars.length; i++) {
            char value = chars[i];
            c.add(value);
        }
        return c;
    }
}
