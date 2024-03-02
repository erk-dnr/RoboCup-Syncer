package syncgod.pubsub.events;

/**
 * Event for frame change.
 *
 * @author tj18b
 */
public class FrameChangeEvent extends AbstractEvent {

    private Long frame;

    public FrameChangeEvent(Long frame) {
        this.frame = frame;
    }

    public Long getFrame() {
        return frame;
    }
}
