package syncgod.pubsub.events;

import syncgod.track.TrackMarker;

public class CreateMarkerEvent extends AbstractEvent {

    private int boxId;
    private final TrackMarker marker;

    public CreateMarkerEvent(final int boxId, final TrackMarker marker) {
        this.marker = marker;
        this.boxId = boxId;
    }

    public TrackMarker getMarker() {
        return marker;
    }

    public int getBoxId() {
        return boxId;
    }
}
