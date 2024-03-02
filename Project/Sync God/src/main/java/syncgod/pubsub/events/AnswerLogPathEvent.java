package syncgod.pubsub.events;

import java.util.List;

public class AnswerLogPathEvent extends AbstractEvent {

    private List<String> paths;

    public AnswerLogPathEvent(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getPaths() {
        return this.paths;
    }
}
