package syncgod.mltparser;

public class MltProducer {

    private String id;
    private String property;
    private String offset;

    public MltProducer(String videoId, String path, String offset) {
        id = videoId;
        property = path;
        this.offset = offset;
    }

    public MltProducer(String videoId, String path) {
        this(videoId, path, "");
    }

    public String getId() {
        return id;
    }

    public String getProperty() {
        return property;
    }

    public String getOffset() {
        return offset;
    }
}
