package com.github.wnebyte.engine.util;

import java.io.File;

public class ResourceUtil {

    public static String getPath(String resourceName) {
        return ResourceUtil.class.getResource(resourceName).getPath();
    }

    public static File getFile(String resourceName) {
        return new File(getPath(resourceName));
    }

    public static String getAbsolutePath(String resourceName) {
        return getFile(resourceName).getAbsolutePath();
    }
}
