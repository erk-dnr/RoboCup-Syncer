package syncgod.pubsub.events;

import javafx.scene.layout.Pane;
import syncgod.track.TrackMarker;

public class DeleteMarkerEvent extends AbstractEvent {

    private final Pane parent;
    private final TrackMarker marker;

    public DeleteMarkerEvent(Pane parent, TrackMarker marker) {
        this.parent = parent;
        this.marker = marker;
    }

    public Pane getParent() {
        return parent;
    }

    public TrackMarker getMarker() {
        return marker;
    }
}

