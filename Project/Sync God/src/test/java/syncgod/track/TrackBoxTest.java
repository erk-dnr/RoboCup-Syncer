package syncgod.track;

import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Test;


public class TrackBoxTest {

    /**
     *
     */
    @Before
    public void setUp() {

    }

    @Test
    public void checkCollision() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TrackPane.class.getResource("/fxml/TrackBox.fxml"));

        TrackBox trackBox = loader.getController();     //TODO repair this test.
        /*
        TrackMarker track0 = new TrackMarker(20L, 60L);
        TrackMarker track1 = new TrackMarker(10L, 50L);
        TrackMarker track2 = new TrackMarker(20L, 40L);
        TrackMarker shouldBe0 = new TrackMarker(20L, 60L);
        TrackMarker shouldBe1 = new TrackMarker(10L, 60L);

        trackBox.addMarker(track0);

        Assert.assertEquals(track0, trackBox.getMarkers().get(0));

        trackBox.addMarker(track1);

        Assert.assertEquals(shouldBe0, trackBox.getMarkers().get(0));
        Assert.assertEquals(1, trackBox.getMarkers().size());

        trackBox.addMarker(track2);

        Assert.assertEquals(shouldBe1, trackBox.getMarkers().get(0));
        Assert.assertEquals(1, trackBox.getMarkers().size());
        */
    }
}
