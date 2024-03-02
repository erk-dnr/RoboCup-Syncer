package syncgod.pubsub.events;

import java.util.List;
import syncgod.track.TrackBox;

public class AnswerTrackDataEvent extends AbstractEvent {

    private List<TrackBox> tracks;

    public AnswerTrackDataEvent(List<TrackBox> tracks) {
        this.tracks = tracks;
    }

    public List<TrackBox> getTracks() {
        return tracks;
    }
}
