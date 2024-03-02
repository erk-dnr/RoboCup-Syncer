package syncgod.pubsub.events;

/**
 * Event for frame change.
 *
 * @author tj18b
 */
public class ProgressAnswerEvent extends AbstractEvent {

    private Long frame;

    public ProgressAnswerEvent(Long frame) {
        this.frame = frame;
    }

    public Long getFrame() {
        return frame;
    }
}
