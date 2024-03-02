package syncgod.mltparser;

public class MltTransition {

    private String in;
    private String out;
    private String trackA;
    private String trackB;

    public MltTransition(final String in,
                         final String out,
                         final String trackA,
                         final String trackB) {
        this.in = in;
        this.out = out;
        this.trackA = trackA;
        this.trackB = trackB;
    }

    public String getIn() {
        return String.valueOf(in);
    }

    public String getOut() {
        return String.valueOf(out);
    }

    public String getBTrack() {
        return String.valueOf(trackB);
    }

    public String getATrack() {
        return String.valueOf(trackA);
    }

    public void setIn(String in) {
        this.in = in;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public void setTrackA(final String trackA) {
        this.trackA = trackA;
    }

    public void setTrackB(final String trackB) {
        this.trackB = trackB;
    }
}
