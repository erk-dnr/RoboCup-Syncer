package syncgod.pubsub.events;

public class LoadProjectEvent extends AbstractEvent {

    private String path;

    public LoadProjectEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
