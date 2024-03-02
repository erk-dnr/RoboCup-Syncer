package syncgod.track;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import syncgod.config.Config;
import syncgod.config.ConfigValue;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.FrameChangeEvent;
import syncgod.pubsub.events.SetFrameEvent;

public class ProgressPane implements Initializable {

    @FXML
    private Pane progressMarkerHolder;
    @FXML
    private Pane progressClickPane;
    @FXML
    private Pane progressMarkerPane;
    @FXML
    private AnchorPane progressTickPane;
    @FXML
    private Label progressPaneLabel;

    private List<ProgressTick> ticks;
    private long maxFrame;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        ticks = new ArrayList<>();
        updateNumberOfTicks(1.0);
        EventBus.getInstance().subscribe(FrameChangeEvent.class.toString(), this);
        progressMarkerPane.setMouseTransparent(true);
        progressMarkerPane.setPickOnBounds(false);
        progressMarkerHolder.setMouseTransparent(true);
        progressMarkerHolder.setPickOnBounds(false);
        progressMarkerPane.heightProperty().addListener(((observable, oldValue, newValue) -> {

        }));
    }


    /**
     * Resizing progress pane and its ticks.
     * @param width width of pane holder
     */
    public void resize(final double width) {
        progressTickPane.setPrefWidth(width - progressPaneLabel.getWidth());
        progressClickPane.setMaxWidth(width - progressPaneLabel.getWidth());
        final double stepWidth = progressTickPane.getPrefWidth() / ticks.size();
        for (int i = 0; i < ticks.size(); i++) {
            ticks.get(i).resize(stepWidth * (i + 1));
        }
    }

    private void updateTicks() {
        long framesPerLabel;
        long timeOfLabel;
        for (int i = 0; i < ticks.size(); i++) {
            framesPerLabel = (long) ((i + 1) * maxFrame / (double) ticks.size());
            timeOfLabel = (long) (framesPerLabel / Config.get(ConfigValue.Fps));
            ticks.get(i).updateLabel(LocalTime.ofSecondOfDay(timeOfLabel).toString());
        }
    }

    private void addTick(final boolean isBigTick) {
        ProgressTick tick = new ProgressTick(isBigTick, progressTickPane.getMinHeight());
        tick.addToParent(progressTickPane);
        ticks.add(tick);
    }

    private void addTicks(int oldValue, final int newValue) {
        if (oldValue > 0) {
            deleteTicks(1);
            oldValue--;
        }
        for (int i = oldValue; i < newValue; i++) {
            boolean isBigTick = (i % 2) > 0;
            addTick(isBigTick);
        }
    }

    private void deleteTicks(final int amount) {
        final int tickSize = ticks.size();
        for (int i = ticks.size() - 1; i >= tickSize - amount; i--) {
            ticks.get(i).deleteFromParent(progressTickPane);
            ticks.remove(i);
        }
    }

    /**
     * Calculates the number of ticks.
     * @param zoom of track pane
     */
    public void updateNumberOfTicks(final double zoom) {
        int zoomTickRatio = 10;
        int newNumberOfTicks = ((int) (zoom * zoomTickRatio));
        if (newNumberOfTicks > ticks.size()) {
            addTicks(ticks.size(), newNumberOfTicks);
        } else if (newNumberOfTicks < ticks.size()) {
            deleteTicks(ticks.size() - newNumberOfTicks);
        }
        updateTicks();
    }

    /**
     * Set max frame and updates time of progress labels.
     * @param maxFrame new maximal frame
     */
    public void setMaxFrame(final long maxFrame) {
        this.maxFrame = maxFrame;
        updateTicks();
    }

    public void receive(final FrameChangeEvent frameChangeEvent) {
        Platform.runLater(() -> {
            long timeOfLabel = (long) (frameChangeEvent.getFrame().doubleValue()
                    / Config.get(ConfigValue.Fps));
            progressPaneLabel.setText(LocalTime.ofSecondOfDay(timeOfLabel).toString());
            progressMarkerPane.setLayoutX(frameChangeEvent.getFrame()
                    / (double) maxFrame * progressClickPane.getWidth());
        });
    }

    @FXML
    private void onMouseClick(MouseEvent mouseEvent) {
        long mouseFrame = (long) (mouseEvent.getX() / progressClickPane.getWidth() * maxFrame);
        EventBus.getInstance().post(new SetFrameEvent(mouseFrame));
    }
}
