package syncgod.videoplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import syncgod.config.Config;
import syncgod.config.ConfigValue;

/**
 * A clock for the frames of the video.
 *
 * @author Dan Häßler
 */
public class FrameClock implements Runnable {

    private final List<VideoWorker> videos;
    private final VideoContainer videoContainer;
    private final long nanoSecPerFrame;
    private final AtomicBoolean pause;
    private long tsLast;
    private long tsDif;

    /**
     * Initialising clock.
     */
    FrameClock(VideoContainer videoContainer) {
        this.videoContainer = videoContainer;
        videos = new ArrayList<>();
        nanoSecPerFrame = 1000000000 / (long) Config.get(ConfigValue.Fps);
        tsLast = 0;
        pause = new AtomicBoolean(true);
    }

    /**
     * Adds a video to the list which the clock sends signal to.
     *
     * @param video to be added
     * @return true if the video has been added, false otherwise
     */
    public boolean add(final VideoWorker video) {
        return videos.add(video);
    }

    /**
     * Removes a video from the list.
     *
     * @param video to be removed
     */
    void delete(final VideoWorker video) {
        videos.remove(video);
    }

    /**
     * Pauses the clock.
     */
    void pause() {
        this.pause.set(true);
    }

    /**
     * Starts the clock.
     */
    public void start() {
        tsLast = System.nanoTime();
        this.pause.set(false);
    }

    @Override
    public void run() {
        if (pause.get()) {
            return;
        }
        tsDif = System.nanoTime() - tsLast;
        if (tsDif > nanoSecPerFrame) {
            tsLast += tsDif;
            videos.forEach(worker -> worker.incrementFrame((int) (tsDif / nanoSecPerFrame)));
            videoContainer.incrementFrame((int) (tsDif / nanoSecPerFrame));
        }
    }
}
