package syncgod.track;


import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import syncgod.pubsub.events.AddVideoEvent;

/**
 *
 * @author sirkpetzold
 */
public class TrackPaneTest extends ApplicationTest {
    
     Parent parent;
     TrackPane ct;
    /**
     *
     */
    public TrackPaneTest() {
    }
    /**
     * For running tests on the JFX application thread.
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TrackPane.class.getResource("/fxml/TrackPane.fxml"));
            parent = loader.load();
            ct = loader.getController();
        
        } catch (IOException ex) {
             //TODO RIGHT ASSERT METHOD
        }
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
        System.out.println("Testing TrackPane");
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
         System.out.println("Done Testing TrackPane");
    }
    
}
