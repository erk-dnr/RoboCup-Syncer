package syncgod.pubsub.events;

/**
 * Event for handling a click on log entry.
 *
 * @author tj18b
 */

public class SetFrameEvent extends AbstractEvent {

    private Long frame;

    public SetFrameEvent(Long frame) {
        this.frame = frame;
    }

    public Long getFrame() {
        return frame;
    }
}
