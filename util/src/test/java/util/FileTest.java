package util;

import java.io.File;
import org.junit.Test;

public class FileTest {

    @Test
    public void fileSeparatorTest00() {
        String separator = File.separator;
        System.out.println("separator: " + separator);
    }

    @Test
    public void filePathSeparatorTest00() {
        String pathSeparator = File.pathSeparator;
        System.out.println("pathSeparator: " + pathSeparator);
    }
}
