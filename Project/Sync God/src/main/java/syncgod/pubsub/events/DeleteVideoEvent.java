package syncgod.pubsub.events;

/**
 * Event when video is closed.
 *
 * @author tj18b
 */
public class DeleteVideoEvent extends AbstractEvent {

    private int index;

    public DeleteVideoEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
