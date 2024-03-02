    /** * Runs the audio/video file */
    public void play(final String filename) {
        final Canvas videoSurface = new Canvas();
        final Frame frame = buildFrame(videoSurface);
        final List<String> vlcArgs = new ArrayList<String>();
        configureParameters(vlcArgs);
        final EmbeddedMediaPlayer mediaPlayer = createPlayer(vlcArgs, videoSurface);
        mediaPlayer.playMedia(filename);
    }