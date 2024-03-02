    /** * Build the player */
    private EmbeddedMediaPlayer createPlayer(final List<String> vlcArgs, 
    final Canvas videoSurface) {
        EmbeddedMediaPlayerComponent component = new EmbeddedMediaPlayerComponent();
        EmbeddedMediaPlayer player = component.getMediaPlayer();
        MediaPlayerFactory factory = new MediaPlayerFactory(
            vlcArgs.toArray(new String[vlcArgs.size()]));
        factory.setUserAgent("vlcj test player");
        player.setVideoSurface(factory.newVideoSurface(videoSurface));
        player.setPlaySubItems(true);
        return player;
    }