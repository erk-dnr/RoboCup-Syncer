package syncgod.pubsub.events;

/**
 * Event for loading log.
 *
 * @author tj18b
 */
public class LoadLogEvent extends AbstractEvent {

    private String path;

    public LoadLogEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
