package syncgod.videoplayer;

import java.io.Closeable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import syncgod.config.Config;
import syncgod.config.ConfigValue;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.DeleteVideoEvent;

/**
 * Wrapper for Video Player initializes worker and schedules them so the future
 * reference can be saved and closed properly.
 *
 * @author Dan Häßler
 */
public class VideoPlayer implements Closeable {

    private final VideoWorker worker;
    private final ScheduledFuture<?> futureVideo;
    private FlowPane videoHolder;
    private ImageView imageView;
    private StackPane stackPane;
    private String path;
    private Long offset;

    /**
     * Creates worker and schedules them.
     *
     * @param path        of video
     * @param videoHolder container for image view
     * @param videoPane   container for resizing
     * @param executor    for scheduling videos
     */
    public VideoPlayer(final String path,
                       final FlowPane videoHolder,
                       final ScrollPane videoPane,
                       final ScheduledExecutorService executor,
                       final boolean isPlaying,
                       final Long offset,
                       final String color) {
        this.videoHolder = videoHolder;
        initGuiComponents(color);
        this.path = path;
        this.offset = offset;
        this.worker = new VideoWorker(path, imageView, videoPane, isPlaying, offset);
        this.futureVideo = executor.scheduleAtFixedRate(
                worker, 0, (long) Config.get(ConfigValue.WorkerRate),
                TimeUnit.MILLISECONDS);
    }

    private void initGuiComponents(String color) {
        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("deleteButton");
        deleteButton.setOnMouseClicked(event -> {
            int index = videoHolder.getChildren().indexOf(stackPane);
            EventBus.getInstance().post(new DeleteVideoEvent(index));
        });
        stackPane = new StackPane();
        imageView = new ImageView();
        stackPane.setStyle("-fx-background-color: " + color + ";"
                    + "-fx-padding: 7 0 0 0;" + "-fx-background-radius: 7 7 0 0");
        stackPane.getChildren().add(imageView);
        stackPane.getChildren().add(deleteButton);
        stackPane.setAlignment(Pos.TOP_RIGHT);
        videoHolder.getChildren().add(stackPane);
    }

    /**
     * Pause video.
     */
    public void pause() {
        worker.pauseVideo();
    }

    /**
     * Play video.
     */
    public void play() {
        worker.playVideo();
    }

    /**
     * Skips amount of frames.
     *
     * @param frames to skip
     */
    public void skipFrame(final double frames) {
        worker.skipFrame(frames);
    }

    /**
     * Sets the video to a specific frame.
     *
     * @param frame to set
     */
    public void setFrame(final double frame) {
        worker.setFrame(frame);
    }

    /**
     * Gets total frames from video.
     *
     * @return total frames
     */
    public long getTotalFrameCount() {
        return (long) worker.getTotalFrames();
    }

    /**
     * Getter for VideoWorker.
     *
     * @return worker
     */
    public VideoWorker getWorker() {
        return worker;
    }

    /**
     * Releases the worker and stops the thread.
     */
    @Override
    public void close() {
        Platform.runLater(() -> {
            imageView.setImage(null);
            stackPane.getChildren().clear();
            videoHolder.getChildren().remove(stackPane);
        });
        worker.close();
        futureVideo.cancel(false);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getOffset() {
        return this.offset;
    }
}
