public static void main(String[] args){
	IMediaReader reader = ToolFactory.makeReader("Szene01.mp4");
	reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

	// create a writer and configure it's parameters from the reader
	IMediaWriter writer = ToolFactory.makeWriter("Szene01_berarbeitet.mp4", reader);

	// create a tool which paints a time stamp onto the video
	IMediaTool addTimeStamp = new TimeStampTool();

	// create a tool which reduces audio volume to 1/10th original
	IMediaTool reduceVolume = new VolumeAdjustTool(0.1);

	// create a tool chain: reader -> addTimeStamp -> reduceVolume -> writer
	reader.addListener(addTimeStamp);
	addTimeStamp.addListener(reduceVolume);
	reduceVolume.addListener(writer);

	// add a viewer to the writer, to see the modified media
	writer.addListener(ToolFactory.makeViewer(AUDIO_VIDEO));

	// read and decode packets from the source file and
	// then encode and write out data to the output file
	while (reader.readPacket() == null);
}