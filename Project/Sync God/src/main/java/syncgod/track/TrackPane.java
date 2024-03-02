package syncgod.track;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.AddMarkerEvent;
import syncgod.pubsub.events.AddVideoEvent;
import syncgod.pubsub.events.AnswerTrackDataEvent;
import syncgod.pubsub.events.CreateMarkerEvent;
import syncgod.pubsub.events.DeleteVideoEvent;
import syncgod.pubsub.events.RequestDataEvent;

/**
 * TrackPane Controller.
 *
 * @author tj18b
 */
public class TrackPane implements Initializable {

    @FXML
    private StackPane trackStackPane;
    @FXML
    private ScrollPane trackPane;
    @FXML
    private FlowPane trackContent;

    private ProgressPane progressPane;
    private final LongProperty maxFrame = new SimpleLongProperty(0);
    private final DoubleProperty zoom = new SimpleDoubleProperty(1.0);
    private final ArrayList<FXMLLoader> tracks;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final double marginX = 50;
    private final Logger log = Logger.getLogger(TrackPane.class.getName());

    @Override
    public void initialize(final URL location,final ResourceBundle resources) {
        FXMLLoader progressLoader
                = new FXMLLoader(getClass().getResource("/fxml/ProgressPane.fxml"));
        try {
            Parent progress = progressLoader.load();
            progress.setPickOnBounds(false);
            trackStackPane.getChildren().add(1, progress);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not load ProgressPane", e);
        }
        progressPane = progressLoader.getController();
        addListener();
        resize();
    }

    /**
     * Constructs a new track pane.
     */
    public TrackPane() {
        tracks = new ArrayList<>();
        EventBus.getInstance().subscribe(AddVideoEvent.class.toString(), this);
        EventBus.getInstance().subscribe(DeleteVideoEvent.class.toString(), this);
        EventBus.getInstance().subscribe(AddMarkerEvent.class.toString(), this);
        EventBus.getInstance().subscribe(RequestDataEvent.class.toString(), this);
        EventBus.getInstance().subscribe(CreateMarkerEvent.class.toString(), this);
    }

    private void addListener() {
        trackPane.viewportBoundsProperty()
                .addListener((observable, oldValue, newValue) -> resize());
        zoom.addListener((observable, oldValue, newValue) -> {
            progressPane.updateNumberOfTicks(newValue.doubleValue());
        });
        maxFrame.addListener((observable, oldValue, newValue) -> {
            tracks.stream().map(controller -> ((TrackBox) controller.getController()))
                    .forEach((box -> box.setMaxFrame(newValue.intValue())));
            progressPane.setMaxFrame(newValue.intValue());
        });
    }

    private void resize() {
        Platform.runLater(() -> {
            double resizeWidth = trackPane.getViewportBounds().getWidth() * zoom.get() - marginX;
            progressPane.resize(resizeWidth);
            tracks.stream().map(controller -> ((TrackBox) controller.getController())).forEach((box
                -> box.resize(resizeWidth)));
        });
    }

    private void addTrack(final long length, final long offset, final String color) {
        FXMLLoader track = new FXMLLoader(getClass().getResource("/fxml/TrackBox.fxml"));
        try {
            Parent root = track.load();

            trackContent.getChildren().add(root);
            tracks.add(track);

            TrackBox controller = track.getController();
            controller.setLength(length);
            controller.setOffset(offset);
            controller.setTrackColor(color);
            controller.setTrackBoxLabel(tracks.size());
            controller.setMaxFrame(maxFrame.get());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not load track box.", e);
        }
    }

    /**
     * Adds a new track and check for new max frame.
     * @param addVideoEvent event containing video info
     */
    public void receive(final AddVideoEvent addVideoEvent) {
        final long offset = addVideoEvent.getVideoOffset();
        final long length = addVideoEvent.getVideoLength();
        addTrack(length, offset, addVideoEvent.getColor());
        if (maxFrame.get() < length + offset) {
            maxFrame.setValue(length + offset);
        }
        resize();
    }

    /**
     * Deletes a track and check for new max frame.
     * @param deleteVideoEvent Event containing video index
     */
    public void receive(final DeleteVideoEvent deleteVideoEvent) {
        if (tracks.isEmpty()) {
            return;
        }
        Parent box = tracks.remove(deleteVideoEvent.getIndex()).getRoot();
        trackContent.getChildren().remove(box);
        TrackBox trackBox;
        for (int i = 0; i < tracks.size(); i++) {
            trackBox = tracks.get(i).getController();
            trackBox.setTrackBoxLabel(i + 1);
            maxFrame.setValue(Math.max(maxFrame.get(), trackBox.getMaxFrame()));
        }
    }

    /**
     * Handles add marker event.
     * @param addMarkerEvent marker which has been added
     */
    public void receive(final AddMarkerEvent addMarkerEvent) {
        tracks.stream().map(controller -> ((TrackBox) controller.getController()))
                .filter(box -> !addMarkerEvent.getBox().equals(box))
                .forEach(box -> box.checkCollision(addMarkerEvent.getMarker()));
    }

    /**
     * Answers to requestdataevent.
     * @param requestDataEvent dummy event
     */
    public void receive(final RequestDataEvent requestDataEvent) {
        EventBus.getInstance().post(new AnswerTrackDataEvent(tracks.stream()
                        .map(controller -> ((TrackBox) controller.getController()))
                        .collect(Collectors.toList())));
    }

    /**
     * Add marker to track.
     * @param createMarkerEvent box id and marker to add
     */
    public void receive(final CreateMarkerEvent createMarkerEvent) {
        FXMLLoader trackBox = this.tracks.get(createMarkerEvent.getBoxId());
        ((TrackBox) trackBox.getController()).addMarker(createMarkerEvent.getMarker());
        resize();
    }

    @FXML
    private void onKeyPressed(final KeyEvent keyEvent) {
        pressedKeys.add(keyEvent.getCode());
        if (pressedKeys.contains(KeyCode.CONTROL)) {
            if (pressedKeys.contains(KeyCode.ADD)) {
                double maxZoomFactor = 10;
                double newZoomValue = Math.min(maxZoomFactor,
                        (double) Math.round((zoom.get() + 0.1) * 100) / 100);
                zoom.set(newZoomValue);
            } else if (pressedKeys.contains(KeyCode.SUBTRACT)) {
                double newZoomValue = Math.max(1.0,
                        (double) Math.round((zoom.get() - 0.1) * 100) / 100);
                zoom.set(newZoomValue);
            }
        }
        if(pressedKeys.contains(KeyCode.SHIFT)){
            tracks.stream().map(controller -> ((TrackBox) controller.getController())).forEach((box
                    -> box.setSnap(false)));
        }
    }

    @FXML
    private void onKeyReleased(final KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());
        resize();
        if(!pressedKeys.contains(KeyCode.SHIFT)){
            tracks.stream().map(controller -> ((TrackBox) controller.getController())).forEach((box
                    -> box.setSnap(true)));
        }
    }
}
