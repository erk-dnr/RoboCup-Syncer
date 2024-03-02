package syncgod.track;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import syncgod.config.Config;
import syncgod.config.ConfigValue;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.AddMarkerEvent;
import syncgod.pubsub.events.DeleteMarkerEvent;
import syncgod.pubsub.events.ProgressAnswerEvent;
import syncgod.pubsub.events.ProgressRequestEvent;

public class TrackBox implements Initializable {
    @FXML
    private HBox trackBoxHolder;
    @FXML
    private Label trackBoxLabel;
    @FXML
    private Label trackBoxFrameLabel;
    @FXML
    private Region trackBoxOffset;
    @FXML
    private AnchorPane trackBoxPane;

    private long length = 0;
    private long offset = 0;
    private long fixedFrame = 0;
    private double previousMouseX = 0;
    private List<TrackMarker> markers;
    private long maxFrame = 0;
    private long thresholdFrame = 0;
    private final long minFrameSize = 5;
    private boolean translationMode = false;
    private TrackMarker selectedMarker;
    private boolean snap;
    private boolean active;
    private byte mouseStart;
    private Long currentFrame;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        snap = false;
        markers = new ArrayList<>();
        mouseStart = 0;
        EventBus.getInstance().subscribe(ProgressAnswerEvent.class.toString(), this);
        EventBus.getInstance().subscribe(DeleteMarkerEvent.class.toString(), this);
        currentFrame = null;
        active = false;
    }

    /**
     * Resizing components.
     * @param width parent width
     */
    void resize(final double width) {
        Platform.runLater(() -> {
            trackBoxHolder.setPrefWidth(width);
            trackBoxOffset.setPrefWidth((width - trackBoxLabel.getWidth())
                    * ((double) offset / (double) maxFrame));
            trackBoxPane.setMaxWidth((width - trackBoxLabel.getWidth())
                    * ((double) length / (double) maxFrame));
            trackBoxPane.setMinWidth((width - trackBoxLabel.getWidth())
                    * ((double) length / (double) maxFrame));
            resizeMarkers();
        });
    }

    private void resizeMarkers() {
        Platform.runLater(() -> {
            markers.forEach(marker -> marker.resize(maxFrame, offset,
                    trackBoxHolder.getWidth() - trackBoxLabel.getWidth()));
        });
    }

    /**
     * Checks for collision with child markers and resolves them, the markers in this track box
     * will be shortened or delete.
     * @param trackMarker marker
     */
    public void checkCollision(final TrackMarker trackMarker) {
        for (int i = 0; i < markers.size(); i++) {
            TrackMarker marker =  markers.get(i);
            if (!marker.checkCollision(trackMarker)) {
                continue;
            }
            if (marker.checkSplit(trackMarker)) {
                TrackMarker splitMarker = new TrackMarker(trackMarker.getFrameEnd(),
                                                          marker.getFrameEnd());
                addMarker(splitMarker);
                marker.setFrameEnd(trackMarker.getFrameStart());
            } else if (marker.collisionLeft(trackMarker)) {
                marker.setFrameStart(trackMarker.getFrameEnd());
            } else if (marker.collisionRight(trackMarker)) {
                marker.setFrameEnd(trackMarker.getFrameStart());
            } else {
                removeMarker(marker);
            }
            if (!marker.isValid()) {
                removeMarker(marker);
                i = -1;
            }
        }
        resizeMarkers();
    }

    /**
     * Handles collision in the same track box.
     * @param trackMarker created marker
     */
    private void mergeMarker(TrackMarker trackMarker) {
        for (int i = 0; i < markers.size(); i++) {
            TrackMarker marker = markers.get(i);
            if (trackMarker.checkCollision(marker)) {
                if (trackMarker.equals(marker)) {
                    continue;
                }
                trackMarker.setFrameStart(Math.min(trackMarker.getFrameStart(),
                                                   marker.getFrameStart()));
                trackMarker.setFrameEnd(Math.max(trackMarker.getFrameEnd(),
                                                 marker.getFrameEnd()));
                removeMarker(marker);
                i = -1;
            }
        }
        if (trackMarker.getFrameEnd() - trackMarker.getFrameStart() < minFrameSize) {
            removeMarker(trackMarker);
        } else {
            EventBus.getInstance().post(new AddMarkerEvent(this, trackMarker));
        }
    }

    /**
     * Adds a marker to the the track box.
     * @param trackMarker marker to add
     */
    void addMarker(final TrackMarker trackMarker) {
        markers.add(trackMarker);
        trackMarker.addToParent(trackBoxPane);
        resizeMarkers();
    }

    private void removeMarker(final TrackMarker trackMarker) {
        trackMarker.removeMarker(trackBoxPane);
        markers.remove(trackMarker);
    }

    private void findMarkerAndSelect(final double x) {
        selectedMarker = null;
        for (TrackMarker trackMarker : markers) {
            if (trackMarker.isInMarkerZone(x)) {
                selectedMarker = trackMarker;
            }
        }
    }

    private long mouseEventToFrame(double mouseX) {
        double ratioX = (mouseX + trackBoxOffset.getWidth())
                / (trackBoxHolder.getWidth() - trackBoxLabel.getWidth());
        return (long) (ratioX * maxFrame);
    }

    @FXML
    private void onMousePressed(final MouseEvent mouseEvent) {
        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }
        double mousePosX = mouseEvent.getX();
        if (selectedMarker == null) {
            fixedFrame = mouseEventToFrame(mousePosX);
            thresholdFrame = fixedFrame;
            selectedMarker = new TrackMarker(fixedFrame, fixedFrame);
            addMarker(selectedMarker);
        } else {
            if (selectedMarker.isInLeftMarkerZone(mousePosX)) {
                fixedFrame = selectedMarker.getFrameEnd();
                mouseStart = -1;

                if (selectedMarker.isDraggableLeftZone(mousePosX)) {
                    translationMode = true;
                    selectedMarker.setCursor(Cursor.W_RESIZE);
                    thresholdFrame = Long.MAX_VALUE;
                } else {
                    thresholdFrame = fixedFrame;
                }
            } else {
                fixedFrame = selectedMarker.getFrameStart();
                mouseStart = 1;
                if (selectedMarker.isDraggableRightZone(mousePosX)) {
                    translationMode = true;
                    selectedMarker.setCursor(Cursor.E_RESIZE);
                    thresholdFrame = Long.MIN_VALUE;
                } else {
                    thresholdFrame = fixedFrame;
                }
            }
        }
        updateBoxFrameLabel();
        previousMouseX = mousePosX;
    }

    @FXML
    private void onMouseMoved(MouseEvent mouseEvent) {
        double mousePosX = mouseEvent.getX();
        findMarkerAndSelect(mousePosX);
        updateBoxFrameLabel();
        if (selectedMarker == null) {
            return;
        }
        selectedMarker.setCursor(Cursor.DEFAULT);

        if (selectedMarker.isDraggableLeftZone(mousePosX)) {
            selectedMarker.setCursor(Cursor.W_RESIZE);
        }

        if (selectedMarker.isDraggableRightZone(mousePosX)) {
            selectedMarker.setCursor(Cursor.E_RESIZE);
        }
    }

    private void updateBoxFrameLabel() {
        if (selectedMarker == null) {
            trackBoxFrameLabel.setVisible(false);
            return;
        }
        trackBoxFrameLabel.setVisible(true);
        trackBoxFrameLabel.setText(selectedMarker.getFrameStart() + " - "
                + selectedMarker.getFrameEnd());
    }

    @FXML
    private void onMouseReleased() {
        if (markers.isEmpty() || selectedMarker == null) {
            return;
        }
        trackBoxFrameLabel.setVisible(false);
        selectedMarker.setCursor(Cursor.DEFAULT);
        mergeMarker(selectedMarker);
        resizeMarkers();
        translationMode = false;
        mouseStart = 0;
    }
    @FXML
    private void onMouseEntered(MouseEvent mouseEvent){
        active = true;
    }

    private void snapToProgress( ){
        if(isNotSnapable() )
            return;
        if( !(currentFrame >= selectedMarker.getFrameEnd() ) && mouseStart == -1){
            selectedMarker.setFrameStart(currentFrame);
            onMouseReleased();
        }
        else if( !(currentFrame <= selectedMarker.getFrameStart() ) && mouseStart == 1){
            selectedMarker.setFrameEnd(currentFrame);
            onMouseReleased();
        }

    }
    private boolean isNotSnapable(){
        return selectedMarker == null || currentFrame == null || currentFrame < mouseEventToFrame(0) || currentFrame > mouseEventToFrame(trackBoxPane.getWidth());
    }
    
    @FXML
    private void onMouseDragged(MouseEvent mouseEvent) {
        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }
        if(snap){
            snapToProgress();
        }
        else {
            double mousePosX = mouseEvent.getX();
            long delta;

            if (translationMode) {
                delta = mouseEventToFrame(mousePosX * Config.get(ConfigValue.MouseResizeSens))
                        - mouseEventToFrame(previousMouseX * Config.get(ConfigValue.MouseResizeSens));
            } else {
                delta = mouseEventToFrame(mousePosX) - mouseEventToFrame(previousMouseX);
            }
            mouseStart = selectedMarker.isInLeftMarkerZone(mousePosX) ? (byte)-1: 1;
            if (mouseEventToFrame(mousePosX) >= thresholdFrame) {
                long newFrameEnd = Math.min(length + offset, Math.max(
                        selectedMarker.getFrameEnd() + delta, fixedFrame + minFrameSize));
                selectedMarker.setFrameEnd(newFrameEnd);
                selectedMarker.setFrameStart(fixedFrame);
            } else {
                long newFrameStart = Math.max(offset,
                        Math.min(selectedMarker.getFrameStart() + delta, fixedFrame - minFrameSize));
                selectedMarker.setFrameStart(newFrameStart);
                selectedMarker.setFrameEnd(fixedFrame);
            }
        }
        updateBoxFrameLabel();
        previousMouseX = mouseEvent.getX();
        resizeMarkers();
    }

    public void setOffset(final long offset) {
        this.offset = offset;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public void setMaxFrame(long maxFrame) {
        this.maxFrame = maxFrame;
    }

    public void setTrackBoxLabel(final int index) {
        trackBoxLabel.setText("Track " + index);
    }

    /**
     * Adding color to track.
     * @param color color as hex string
     */
    void setTrackColor(final String color) {
        trackBoxHolder.setStyle("-fx-border-color: #000");
        trackBoxPane.setStyle("-fx-background-color : linear-gradient(" + color + " 50%, #999 "
                + "100%);");
    }

    public List<TrackMarker> getMarkers() {
        return markers;
    }

    long getMaxFrame() {
        return maxFrame;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrackBox trackBox = (TrackBox) o;
        return length == trackBox.length
                && offset == trackBox.offset
                && fixedFrame == trackBox.fixedFrame
                && maxFrame == trackBox.maxFrame
                && Objects.equals(trackBoxHolder, trackBox.trackBoxHolder)
                && Objects.equals(trackBoxLabel, trackBox.trackBoxLabel)
                && Objects.equals(trackBoxOffset, trackBox.trackBoxOffset)
                && Objects.equals(trackBoxPane, trackBox.trackBoxPane)
                && Objects.equals(markers, trackBox.markers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackBoxHolder, trackBoxLabel, trackBoxOffset, trackBoxPane,
                length, offset, markers, fixedFrame, maxFrame);
    }

    @FXML
    public void onMouseExited(MouseEvent mouseEvent) {
        trackBoxFrameLabel.setVisible(false);
        active = false;
    }

    public void receive(final ProgressAnswerEvent frameRecieveEvent) {
        currentFrame = frameRecieveEvent.getFrame();
    }

    public void receive(final DeleteMarkerEvent deleteEvent) {
        if(deleteEvent.getParent() == trackBoxPane)
            markers.remove(deleteEvent.getMarker());
    }

    public void setSnap(boolean release){
        if(active && !release) {
            snap = true;
            EventBus.getInstance().post(new ProgressRequestEvent() );
        }
        else
            snap = false;
    }
}
