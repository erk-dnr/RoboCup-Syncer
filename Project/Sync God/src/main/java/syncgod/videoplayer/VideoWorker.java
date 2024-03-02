package syncgod.videoplayer;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_COUNT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_POS_FRAMES;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_POS_MSEC;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import syncgod.util.FxUtil;

/**
 * A worker for a video.
 *
 * @author Dan Häßler
 */
public final class VideoWorker implements Runnable {

    private final VideoCapture video;
    private final AtomicBoolean isPlaying;
    private final AtomicInteger currentFrame;
    private final Mat frameMat;
    private final double frameRate;
    private final double videoRatio;
    private final double totalFrames;
    private final double offset;
    private final ImageView imageView;
    private final int videoMargin = 5;

    /**
     * Initializing worker and setting up components.
     *
     * @param path to video
     * @param view image view to write frames to
     * @param pane for resizing
     */

    VideoWorker(final String path, final ImageView view,
                       final ScrollPane pane, final boolean playing, final double offset) {
        video = new VideoCapture();
        video.open(path);
        frameMat = new Mat();
        this.offset = offset;
        isPlaying = new AtomicBoolean(playing);
        currentFrame = new AtomicInteger(1);
        imageView = view;
        totalFrames = video.get(CV_CAP_PROP_FRAME_COUNT);
        videoRatio = video.get(CV_CAP_PROP_FRAME_HEIGHT) / video.get(CV_CAP_PROP_FRAME_WIDTH);
        frameRate = video.get(CV_CAP_PROP_FPS);

        fitImageViewSize(pane.getViewportBounds().getWidth());
        if (video.isOpened()) {
            drawNextImage();
            currentFrame.set((int) video.get(CV_CAP_PROP_POS_FRAMES)  - (int) offset);
        }
    }

    /**
     * Fit image view size to available width, components will be half the width.
     * @param width parent width
     */
    public void fitImageViewSize(final double width) {
        Platform.runLater(() -> {
            final double fitWidth = Math.floor((width - videoMargin) / 2);
            final double fitHeight = (int) (videoRatio * fitWidth);
            imageView.setFitWidth(fitWidth);
            imageView.setFitHeight(fitHeight);
        });
    }


    /**
     * Plays video.
     */
    public void playVideo() {
        isPlaying.set(true);
    }

    /**
     * Pauses video.
     */
    public void pauseVideo() {
        isPlaying.set(false);
    }

    /**
     * Closes the video properly.
     */
    public void close() {
        pauseVideo();
        if (this.video.isOpened()) {
            this.video.release();
        }
    }

    public double getTotalFrames() {
        return totalFrames;
    }

    /**
     * Increments target frame.
     *
     * @param frames to increment
     */
    public void incrementFrame(final int frames) {
        if (currentFrame.get() + frames > totalFrames) {
            isPlaying.set(false);
        }
        if (!video.isOpened() || !isPlaying.get()) {
            return;
        }
        currentFrame.getAndAdd(frames);
    }

    /**
     * Skip n frames.
     *
     * @param skipFrames to be skipped
     */
    public void skipFrame(final double skipFrames) {
        if (!video.isOpened()) {
            return;
        }
        double lastFrame = currentFrame.get() + skipFrames;
        setFrame(lastFrame + skipFrames);
    }

    /**
     * Set last frame to n and frame from grabber to n-1 and draws next image.
     *
     * @param frame to set the position to
     */
    public void setFrame(final double frame) {
        if (isPlaying.get()) {
            return;
        }
        if (!video.isOpened()) {
            return;
        }
        double setFrame = Math.min(frame, totalFrames) - offset;
        currentFrame.set((int) (setFrame));

        if (setFrame < 1) {
            setFrame = 1;
        } else if (setFrame > totalFrames) {
            setFrame = totalFrames;
        }
        double toFrame = setFrame - 1;
        double frameTime = 1000.0 * toFrame / frameRate;
        video.set(CV_CAP_PROP_POS_MSEC, frameTime);
        drawNextImage();
    }

    /**
     * Get and draw next image.
     */
    private void drawNextImage() {
        if (video.read(frameMat)) {
            updateImageView(imageView, FxUtil.mat2Image(frameMat.clone()));
        }
    }

    private void skipSingleFrame() {
        video.grab();
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread.
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(final ImageView view, final Image image) {
        FxUtil.onFxThread(view.imageProperty(), image);
    }

    /**
     * Draws next image if last frame is not current frame.
     */
    @Override
    public void run() {
        if (!video.isOpened() || !isPlaying.get()) {
            return;
        }
        long diffFrame = (long) (currentFrame.get() - video.get(CV_CAP_PROP_POS_FRAMES));
        if (diffFrame > 0) {
            if (diffFrame > 3) {
                skipSingleFrame();
            }
            drawNextImage();
        }
    }
}
