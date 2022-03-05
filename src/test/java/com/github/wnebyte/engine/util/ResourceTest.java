package com.github.wnebyte.engine.util;

import org.junit.Test;

import java.io.File;

public class ResourceTest {

    @Test
    public void test00() {
        File file = getFile("/images/testImage.png");
        String path = file.getAbsolutePath();
        File dup = getFile(path);
        System.out.println(dup);
    }

    public static String getPath(String resourceName) {
        File file = new File(resourceName);
        return file.exists() ? file.getAbsolutePath() :
                ResourceUtil.class.getResource(resourceName).getPath();
    }

    public static File getFile(String resourceName) {
        return new File(getPath(resourceName));
    }

    public static String getAbsolutePath(String resourceName) {
        return getFile(resourceName).getAbsolutePath();
    }
}
