package syncgod.videoplayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import syncgod.config.Config;
import syncgod.config.ConfigValue;
import syncgod.menu.SelectVideoMenu;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.*;
import syncgod.util.FxUtil;

/**
 * Video Pane Controller which assigns functions to the gui elements.
 *
 * @author tj18b
 */
public class VideoContainer implements Initializable {

    private static final ScheduledThreadPoolExecutor EXECUTOR
            = (ScheduledThreadPoolExecutor) Executors
                .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final FrameClock frameCounter;
    private final AtomicLong globalFrames;
    private final List<VideoPlayer> videos;
    private FadeTransition fadeInTransition;
    private FadeTransition fadeOutTransition;
    private long maxFrame;
    @FXML
    private FlowPane videoHolder;
    @FXML
    private ScrollPane videoScrollPane;
    @FXML
    private ToggleButton togglePlayButton;
    @FXML
    private VBox controllerMenu;
    @FXML
    private Button forwardButton;
    @FXML
    private Button fastForwardButton;
    @FXML
    private Button backButton;
    @FXML
    private Button fastBackButton;

    private static final Logger LOG = Logger.getLogger(VideoContainer.class.getName());

    /**
     * Initializes the clock, videos can be added whenever.
     */
    public VideoContainer() {
        globalFrames = new AtomicLong(0);
        videos = new ArrayList<>();
        frameCounter = new FrameClock(this);
        maxFrame = 0;
        EXECUTOR.setRemoveOnCancelPolicy(true);
        EXECUTOR.submit(frameCounter);
        EXECUTOR.scheduleAtFixedRate(frameCounter, 0,
                (long) Config.get(ConfigValue.ClockRate),
                TimeUnit.MILLISECONDS);
        EventBus.getInstance().subscribe(LoadVideoEvent.class.toString(), this);
        EventBus.getInstance().subscribe(SetFrameEvent.class.toString(), this);
        EventBus.getInstance().subscribe(DeleteVideoEvent.class.toString(), this);
        EventBus.getInstance().subscribe(ProgressSkipEvent.class.toString(), this);
        EventBus.getInstance().subscribe(RequestDataEvent.class.toString(), this);
        EventBus.getInstance().subscribe(ClearVideosEvent.class.toString(), this);
        EventBus.getInstance().subscribe(ProgressRequestEvent.class.toString(), this);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        controllerMenu.setPickOnBounds(false);
        videoScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue)
            -> videos.forEach(x -> x.getWorker().fitImageViewSize(newValue.getWidth())));
        fadeInTransition = new FadeTransition(Duration.millis(500), controllerMenu);
        fadeInTransition.setInterpolator(Interpolator.EASE_IN);
        fadeInTransition.setToValue(1.0);
        togglePlayButton.setVisible(false);
        fadeOutTransition = new FadeTransition(Duration.millis(350), controllerMenu);
        fadeOutTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeOutTransition.setToValue(0.0);
        showSkipButtons(false);
    }

    /**
     * Toggle start clock and each video in the list.
     */
    public void onClickPlayPause() {
        if (togglePlayButton.isSelected()) {
            playVideos();
        } else {
            pauseVideos();
        }
    }

    private void playVideos() {
        showSkipButtons(false);
        frameCounter.start();
        videos.forEach(VideoPlayer::play);
    }

    private void pauseVideos() {
        showSkipButtons(true);
        frameCounter.pause();
        videos.forEach(VideoPlayer::pause);
    }

    private void showSkipButtons(final boolean show) {
        boolean setVis = show;
        if (videos.isEmpty()) {
            setVis = false;
        }
        forwardButton.setVisible(setVis);
        fastForwardButton.setVisible(setVis);
        backButton.setVisible(setVis);
        fastBackButton.setVisible(setVis);

        fastBackButton.setDisable(!setVis);
        forwardButton.setDisable(!setVis);
        fastForwardButton.setDisable(!setVis);
        backButton.setDisable(!setVis);
    }

    /**
     * Skips one video frame.
     */
    public void onClickForward() {
        skipFrame(1);
    }

    /**
     * Rewinds one video frame.
     */
    public void onClickBack() {
        skipFrame(-1);
    }

    /**
     * Skips a configurable amount of video frames.
     */
    public void onClickFastForward() {
        skipFrame((int) Config.get(ConfigValue.SkipFrameAmount));
    }

    /**
     * Rewinds a configurable amount of video frames in.
     */
    public void onClickFastBack() {
        skipFrame((int) -Config.get(ConfigValue.SkipFrameAmount));
    }

    @FXML
    public void onMouseEntered() {
        fadeInTransition.play();
    }

    @FXML
    public void onMouseExited() {
        fadeOutTransition.play();
    }

    private void setFrame(final long frame) {
        togglePlayButton.setSelected(false);
        pauseVideos();
        final long inBoundFrame = Math.min(Math.max(0, frame), maxFrame);
        globalFrames.set(inBoundFrame);
        videos.forEach(player -> player.setFrame(inBoundFrame));
        EventBus.getInstance().post(new FrameChangeEvent(inBoundFrame));
    }

    private void skipFrame(final long frame) {
        togglePlayButton.setSelected(false);
        pauseVideos();
        setFrame(globalFrames.get() + frame);
    }



    /**
     * Increments target frame.
     *
     * @param frames to increment
     */
    void incrementFrame(final int frames) {
        if (togglePlayButton.isSelected()) {
            globalFrames.set(Math.min(globalFrames.get() + frames, maxFrame));
            EventBus.getInstance().post(new FrameChangeEvent(globalFrames.get()));
        }
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            List<String> paths =
                    db.getFiles().stream().map(File::getAbsolutePath).collect(Collectors.toList());

            List<String> projects = new ArrayList<>();

            for (int i = paths.size() - 1; i >= 0; i--) {
                String s =  paths.get(i);
                if (s.endsWith(".mlt")) {
                    projects.add(s);
                    paths.remove(i);
                }
            }

            if (!projects.isEmpty()) {
                EventBus.getInstance().post(new LoadProjectEvent(projects.get(0)));
            }

            if (!paths.isEmpty()) {
                FXMLLoader loader = FxUtil.newWindow("/fxml/menu/SelectVideoMenu.fxml",
                        "Enter Offsets", videoScrollPane.getScene().getWindow());
                SelectVideoMenu controller = loader.getController();
                controller.setUp(paths);
            }
        }
    }


    @FXML
    private void onDragOver(DragEvent event) {

        List<String> validExtensions = Arrays.asList("mp4", "mlt");
        List<File> files = event.getDragboard().getFiles();

        if ((event.getGestureSource() != videoScrollPane) && !files.isEmpty()) {
            if (!validExtensions.containsAll(event.getDragboard().getFiles().stream()
                            .map(file -> getExtension(file.getName()))
                            .collect(Collectors.toList()))) {
                event.consume();
                return;
            }
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }


    private String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(i + 1).toLowerCase();
        }

        return extension;
    }



    /**
     * After video is closed it has to be removed from clock and the list.
     * @param deleteVideoEvent event which contains id
     */
    public void receive(DeleteVideoEvent deleteVideoEvent) {
        VideoPlayer vid = videos.remove(deleteVideoEvent.getIndex());
        frameCounter.delete(vid.getWorker());
        frameCounter.pause();
        videos.forEach(VideoPlayer::pause);
        togglePlayButton.setSelected(false);
        vid.close();
        updateMaxFrame();
        if (videos.isEmpty()) {
            globalFrames.set(0);
            showSkipButtons(false);
            togglePlayButton.setVisible(false);
            togglePlayButton.setDisable(true);
        }
    }

    /**
     * Loads a new video and adds it to list and clock.
     *
     * @param loadVideoEvent event which contains path
     */
    public void receive(final LoadVideoEvent loadVideoEvent) {
        String color = FxUtil.createRandomColor();
        VideoPlayer player = new VideoPlayer(loadVideoEvent.getPath(),
                videoHolder, videoScrollPane, EXECUTOR, togglePlayButton.isSelected(),
                loadVideoEvent.getOffset(), color);
        videos.add(player);
        player.setFrame(globalFrames.get());
        frameCounter.add(player.getWorker());
        updateMaxFrame();
        EventBus.getInstance().post(new AddVideoEvent(loadVideoEvent.getOffset(),
                player.getTotalFrameCount(), color));
        showSkipButtons(true);
        togglePlayButton.setVisible(true);
        togglePlayButton.setDisable(false);
    }

    /**
     * Skip event when clicked on log event.
     * @param setFrameEvent event which contains frame from log entry
     */
    public void receive(final SetFrameEvent setFrameEvent) {
        setFrame(setFrameEvent.getFrame());
    }

    /**
     * Skip event from progress marker.
     * @param progressSkipEvent event for frame skip event
     */
    public void receive(final ProgressSkipEvent progressSkipEvent) {
        setFrame(progressSkipEvent.getFrame().intValue());
    }


    public void receive(final RequestDataEvent requestDataEvent) {
        EventBus.getInstance().post(new AnswerVideoDataEvent(this.videos));
    }

    public void receive(final ProgressRequestEvent revieveEvent) {
        EventBus.getInstance().post(new ProgressAnswerEvent(globalFrames.get() ) );
    }


    /**
     * Delete all video when event fired.
     * @param clearVideosEvent dummyEvent
     */
    public void receive(final ClearVideosEvent clearVideosEvent) {
        for (int i = videos.size() - 1; i >= 0; i--) {
            EventBus.getInstance().post(new DeleteVideoEvent(i));
        }
    }

    private void updateMaxFrame() {
        for (int i = 0; i < videos.size(); i++) {
            maxFrame = Math.max(maxFrame,
                    videos.get(i).getTotalFrameCount() + videos.get(i).getOffset());
        }
    }
}
