public static void main(String[] args){
	IMediaReader reader = ToolFactory.makeReader("Szene01.MP4");
	IMediaViewer viewer = ToolFactory.makeViewer();
	reader.addListener(viewer);

	while (reader.readPacket() == null);
}