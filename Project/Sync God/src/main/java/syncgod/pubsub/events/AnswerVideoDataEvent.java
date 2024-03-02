package syncgod.pubsub.events;

import java.util.List;
import syncgod.videoplayer.VideoPlayer;

public class AnswerVideoDataEvent extends AbstractEvent {

    private List<VideoPlayer> players;

    public AnswerVideoDataEvent(List<VideoPlayer> players) {
        this.players = players;
    }

    public List<VideoPlayer> getPlayer() {
        return players;
    }
}
