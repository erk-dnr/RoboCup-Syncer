package syncgod.mltparser;

public class MltEntry {

    private String producerId;
    private String out;
    private String in;

    public MltEntry(String id, String in, String out) {
        this.producerId = id;
        this.in = in;
        this.out = out;
    }

    public String getProducer() {
        return producerId;
    }

    public String getIn() {
        return String.valueOf(in);
    }

    public Long getLongIn() {
        return Long.parseLong(in);
    }

    public String getOut() {
        return String.valueOf(out);
    }
}
