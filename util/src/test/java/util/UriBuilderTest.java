package util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import com.github.wnebyte.util.UriBuilder;

public class UriBuilderTest {

    @Test
    public void test00() {
        UriBuilder builder = new UriBuilder();
        builder.setAuthority("C:/Users/ralle/dev/java/EngineTest");
        builder.appendPath("assets");
        builder.appendPath("scenes");
        String uri = builder.build();
        File file = new File(uri);
        System.out.println(uri);
        System.out.println(file);
        Assert.assertTrue(file.exists());
    }

    @Test
    public void test01() {
        UriBuilder builder = new UriBuilder();
        builder.setAuthority("C:/Users/ralle/dev/java/EngineTest");
        String uri = builder.build();
        System.out.println(uri);
        Assert.assertEquals("C:\\Users\\ralle\\dev\\java\\EngineTest", uri);
    }

    @Test
    public void test02() {
        UriBuilder builder = new UriBuilder();
        builder.setAuthority("C:/Users/ralle/dev/java/EngineTest");
        builder.appendPath("assets");
        String uri = builder.build();
        System.out.println(uri);
        Assert.assertEquals("C:\\Users\\ralle\\dev\\java\\EngineTest\\assets", uri);
    }
}
