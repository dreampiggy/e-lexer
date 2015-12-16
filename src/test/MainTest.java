package test;

import com.elexer.Main;
import com.elexer.tool.Config;
import com.elexer.type.Grammar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class MainTest {
    private InputStream in,first,follow;
    private String inResult,firstResult,followResult;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        Config.level = Level.SEVERE;
        String userPath = System.getProperty("user.dir");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        in = new FileInputStream(userPath + "/test/test.l");
        first = new FileInputStream(userPath + "/test/test-first.l");
        follow = new FileInputStream(userPath + "/test/test-follow.l");
        inResult = readToString(userPath + "/test/test-result.txt");
        firstResult = readToString(userPath + "/test/test-first-result.txt");
//        followResult = readToString(userPath + "/test/test-follow-result.txt");
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void testMain() throws Exception {
        String[] args = {};

        Config.input = in;
        Main.main(args);
        assertEquals(outContent.toString(), inResult);
        outContent.reset();
    }

    @Test
    public void testFirst() throws Exception {
        String[] args = {};

        Config.input = first;
        Main.main(args);
        assertEquals(outContent.toString(), firstResult);
        outContent.reset();
    }
    public static String readToString(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String text = new String(bytes);
        return text;
    }
}