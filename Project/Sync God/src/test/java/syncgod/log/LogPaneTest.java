package syncgod.log;

import java.net.URL;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sirkpetzold
 */
public class LogPaneTest {
    
    /**
     *
     */
    public LogPaneTest() {
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
     * Test of initialize method, of class LogPane.
     */
    @Test
    public void testInitialize() {
        System.out.println("initialize");
        URL url = getClass().getResource("/fxml/LogPane.fxml");
        ResourceBundle rb = null;
    }

    /**
     * Test of onClick method, of class LogPane.
     */
    @Test
    public void testOnClick() {
        System.out.println("onClick");
    }

    /**
     * Test of onDragDropped method, of class LogPane.
     */
    @Test
    public void testOnDragDropped() {
        System.out.println("onDragDropped");
    }
    
}
