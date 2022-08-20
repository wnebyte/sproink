package com.github.wnebyte.sproink.util;

import java.io.File;

public class ResourceUtil {

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
