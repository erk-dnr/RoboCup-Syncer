package syncgod.pubsub.events;

import syncgod.track.TrackBox;
import syncgod.track.TrackMarker;

public class AddMarkerEvent extends AbstractEvent {

    private final TrackBox box;
    private final TrackMarker marker;

    public AddMarkerEvent(TrackBox box, TrackMarker marker) {
        this.box = box;
        this.marker = marker;
    }

    public TrackBox getBox() {
        return box;
    }

    public TrackMarker getMarker() {
        return marker;
    }
}
