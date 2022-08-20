package com.github.wnebyte.sproink.util;

import java.util.Set;
import java.util.Collection;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;
import com.github.wnebyte.sproink.Main;
import com.github.wnebyte.sproink.core.ecs.Component;

public class ReflectionUtilTest {

    @Test
    public void test00() {
        Collection<URL> c = ClasspathHelper.forPackage("", Main.class.getClassLoader());
        Assert.assertEquals(3, c.size());
    }

    @Test
    public void test01() {
        Set<Class<? extends Component>> c = ReflectionUtil.getAllComponentSubTypes(Main.class.getClassLoader());
        for (Class<? extends Component> cls : c) {
            System.out.println(cls.getSimpleName());
        }
    }
}
