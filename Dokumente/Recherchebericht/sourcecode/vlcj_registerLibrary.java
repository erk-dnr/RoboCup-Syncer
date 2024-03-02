    private void registerLibrary() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "d:/vlc-2.2.1");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        LibXUtil.initialise();
    }