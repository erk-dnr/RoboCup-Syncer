package syncgod.pubsub.events;

public class ProgressSkipEvent extends AbstractEvent {

    private Long frame;

    public ProgressSkipEvent(Long frame) {
        this.frame = frame;
    }

    public Long getFrame() {
        return frame;
    }
}
