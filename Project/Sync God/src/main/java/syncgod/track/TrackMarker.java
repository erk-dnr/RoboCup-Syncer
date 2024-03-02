package syncgod.track;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.DeleteMarkerEvent;

public class TrackMarker extends Region {

    private final int dragZoneWidth = 5;
    private Long frameStart;
    private Long frameEnd;

    /**
     * Constructs new track marker.
     * @param frameStart start of marker
     * @param frameEnd end of marker
     */
    public TrackMarker(final Long frameStart, final Long frameEnd) {
        super();
        this.frameStart = frameStart;
        this.frameEnd = frameEnd;
        this.getStyleClass().add("markerRegion");
    }

    void addToParent(final Pane parent) {
        this.setMinHeight(parent.getMinHeight());
        parent.getChildren().add(this);
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (event.getClickCount() == 2) {
                    removeMarker(parent);
                }
            }
        });
    }

    void removeMarker(final Pane parent) {
        parent.getChildren().remove(this);
        EventBus.getInstance().post(new DeleteMarkerEvent(parent, this));
    }

    boolean isDraggableRightZone(final double x) {
        return x >= Math.max(this.getLayoutX() + this.getWidth() - dragZoneWidth,
                this.getLayoutX() + this.getWidth() / 2);
    }

    boolean isDraggableLeftZone(final double x) {
        return x <= Math.min(this.getLayoutX() + dragZoneWidth,
                this.getLayoutX() + this.getWidth() / 2);
    }

    boolean isInLeftMarkerZone(final double x) {
        return this.getLayoutX() <= x && x <= this.getLayoutX() + this.getWidth() / 2;
    }

    boolean isInMarkerZone(final double x) {
        return (this.getLayoutX() <= x
                && x <= this.getLayoutX() + this.getWidth());
    }

    /**
     * Resize marker.
     * @param maxFrame max frame
     * @param offset offset of marker
     * @param width width of parent
     */
    void resize(final Long maxFrame, final Long offset, final double width) {
        Platform.runLater(() -> {
            this.setPrefWidth(Math.floor((int) (frameEnd - frameStart)
                    * (int) width / (double) maxFrame));
            this.setLayoutX(Math.floor((int) (frameStart - offset)
                    * (int) width / (double) maxFrame));
        });
    }

    /**
     * Checks for any collisions.
     * @param trackMarker marker
     * @return if collides
     */
    public boolean checkCollision(final TrackMarker trackMarker) {
        return (collisionLeft(trackMarker)
                || collisionRight(trackMarker)
                || checkSplit(trackMarker));
    }

    /**
     * Check for collision from left side.
     * @param trackMarker marker
     * @return if collision from left
     */
    public boolean collisionLeft(final TrackMarker trackMarker) {
        return (trackMarker.frameStart <= frameStart && frameStart <= trackMarker.frameEnd);
    }

    /**
     * Check for collision from right side.
     * @param trackMarker marker
     * @return if collision from right
     */
    public boolean collisionRight(final TrackMarker trackMarker) {
        return (trackMarker.frameStart <= frameEnd && frameEnd <= trackMarker.frameEnd);
    }

    /**
     * Check if this marker includes given marker.
     * @param trackMarker marker
     * @return if includes given marker
     */
    public boolean checkSplit(final TrackMarker trackMarker) {
        return (trackMarker.frameStart > frameStart && trackMarker.frameEnd < frameEnd);
    }

    /**
     * Checks validity.
     * @return if start < end
     */
    boolean isValid() {
        return frameEnd - frameStart > 0;
    }


    public Long getFrameStart() {
        return frameStart;
    }

    void setFrameStart(Long frameStart) {
        this.frameStart = frameStart;
    }

    public Long getFrameEnd() {
        return frameEnd;
    }

    void setFrameEnd(Long frameEnd) {
        this.frameEnd = frameEnd;
    }
}
