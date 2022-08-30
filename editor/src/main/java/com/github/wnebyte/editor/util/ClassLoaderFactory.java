package com.github.wnebyte.editor.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderFactory {

    public static ClassLoader newInstance(String dir) {
        try {
            return new URLClassLoader(new URL[] {
                    new File(dir).toURI().toURL()
            }, ClassLoaderFactory.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
