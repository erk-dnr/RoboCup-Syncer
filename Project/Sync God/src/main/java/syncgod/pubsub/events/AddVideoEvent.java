package syncgod.pubsub.events;

/**
 * Event for adding video.
 *
 * @author tj18b
 */

public class AddVideoEvent extends AbstractEvent {

    private final Long videoOffset;
    private final Long videoLength;
    private final String color;

    /**
     * Event for adding video into the video.
     * @param videoOffset offset in frames of video
     * @param videoLength length in frames of video
     * @param color color of the video container
     */
    public AddVideoEvent(Long videoOffset, Long videoLength, String color) {
        this.videoOffset = videoOffset;
        this.videoLength = videoLength;
        this.color = color;
    }

    public Long getVideoOffset() {
        return videoOffset;
    }

    public Long getVideoLength() {
        return videoLength;
    }

    public String getColor() {
        return color;
    }
}
