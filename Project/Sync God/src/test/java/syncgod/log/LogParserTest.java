package syncgod.log;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sirkpetzold
 */
public class LogParserTest {

    /**
     *
     */
    public LogParserTest() {
    }

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     *
     */
    @Before
    public void setUp() {
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class LogParser.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseGood() throws IOException {
        System.out.println("Parse Good");
        String path = "src/main/resources/test/log_test.txt";
        LogEntry firstEntry = new LogEntry("13.14.24", "Playing");
        LogEntry secondEntry = new LogEntry("13.17.35", "Player Pushing Blue 2");
        List<LogEntry> result = LogParser.parse(path);
        assertTrue(firstEntry.equals(result.get(0)));
        assertTrue(secondEntry.equals(result.get(1)));
    }
}
