public class SamplePlayer {
    public SamplePlayer() {
        registerLibrary();
    }

    /** * Runs the audio/video file */
    public void play(final String filename) {
        final Canvas videoSurface = new Canvas();
        final Frame frame = buildFrame(videoSurface);
        final List<String> vlcArgs = new ArrayList<String>();
        configureParameters(vlcArgs);
        final EmbeddedMediaPlayer mediaPlayer = createPlayer(vlcArgs, videoSurface);
        mediaPlayer.playMedia(filename);
    }

    /**
     * * Important: Notice where is the libvlc, which contains all native functions
     * to manipulate the player * * Windows: libvlc.dll * Linux: libvlc.so
     */
    private void registerLibrary() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "d:/vlc-2.2.1");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        LibXUtil.initialise();
    }

    /** * Create the frame where movie will be played */
    private Frame buildFrame(final Canvas videoSurface) {
        final Frame f = new Frame("Test Player");
        f.setSize(800, 600);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setLayout(new BorderLayout());
        f.add(videoSurface, BorderLayout.CENTER);
        f.setVisible(true);
        return f;
    }

    /** * Configure VLC parameters */
    private void configureParameters(final List<String> vlcArgs) {
        vlcArgs.add("--no-plugins-cache");
        vlcArgs.add("--no-video-title-show");
        vlcArgs.add("--no-snapshot-preview");
        // Important, if this parameter would not be set on Windows, the app won't work
          if (RuntimeUtil.isWindows()) {
            vlcArgs.add("--plugin-path=D:\\vlc-2.2.1\\plugins");
        }
    }

    /** * Build the player */
    private EmbeddedMediaPlayer createPlayer(final List<String> vlcArgs, final Canvas videoSurface) {
        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerComponent.getMediaPlayer();
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
        mediaPlayerFactory.setUserAgent("vlcj test player");
        embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
        embeddedMediaPlayer.setPlaySubItems(true);
        return embeddedMediaPlayer;
    }

    public static void main(String[] args) throws InterruptedException {
        SamplePlayer player = new SamplePlayer(); // Could be MP4, AVI, MOV, MKV, WMA, MPG, MP3, WAV, etc.
                                                            // player.play("D:\\videos\\video.mp4"); // Waits until the
                                                            // player window be closed Thread.currentThread().join(); }
                                                            // }
    }
}