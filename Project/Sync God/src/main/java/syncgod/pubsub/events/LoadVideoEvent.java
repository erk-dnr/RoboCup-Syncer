package syncgod.pubsub.events;

/**
 * Event for loading video.
 *
 * @author tj18b
 */
public class LoadVideoEvent extends AbstractEvent {

    private final String path;
    private final Long offset;

    public LoadVideoEvent(String path, Long offset) {
        this.path = path;
        this.offset = offset;
    }

    public Long getOffset() {
        return offset;
    }

    public String getPath() {
        return path;
    }
}
