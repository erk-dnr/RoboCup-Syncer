package syncgod.videoplayer;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author sirkpetzold
 */
public class VideoContainerTest extends ApplicationTest {

    Parent parent;
    VideoContainer container;
    
    /**
     *
     */
    @Before
    public void setUp() {
        /*try {
            FXMLLoader loader = new FXMLLoader(VideoContainer.class.getResource("/fxml/VideoContainer.fxml"));
            parent = loader.load();
            container = loader.getController();
        } catch (IOException ex) {
            //TODO RIGHT ASSERT METHOD
        }*/
    }

    @Test
    public void loadVideo() {
        //TODO Repair test
        //container.receive(new LoadVideoEvent(VideoContainer.class.getResource("test/Szene01.mp4").getPath(), 0L));
        //container.receive(new LoadVideoEvent(VideoContainer.class.getResource("test/Szene01.mp4").getPath(), 30L));
    }
    
}
