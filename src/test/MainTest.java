package test;

import com.elexer.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileInputStream;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        String testPath = System.getProperty("user.dir");
        InputStream in = new FileInputStream(testPath + "/test/test.l");
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void testMain() throws Exception {
        String[] args = {};
        Main.main(args);// should manually set scanner to FileInputStream
        assertEquals(outContent.toString(),"left: S\n" +
                "right: A\n" +
                "\n" +
                "left: S\n" +
                "right: a\n" +
                "\n" +
                "left: S\n" +
                "right: 1\n" +
                "\n" +
                "left: S{1}\n" +
                "right: F\n" +
                "\n" +
                "left: S{1}\n" +
                "right: S{1}\n" +
                "right: A\n" +
                "\n" +
                "left: F\n" +
                "right: ε\n" +
                "\n" +
                "left: F{*}\n" +
                "right: S{1}\n" +
                "right: e\n" +
                "right: ε\n" +
                "\n" +
                "left: F{*}\n" +
                "right: F\n" +
                "right: 1\n" +
                "right: 2\n" +
                "right: 3");
    }
}