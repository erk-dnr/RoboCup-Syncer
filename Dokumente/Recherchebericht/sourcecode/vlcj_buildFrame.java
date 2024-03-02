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
