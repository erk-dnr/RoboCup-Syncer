package syncgod.mltparser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MltTransitionTest {

    @Test
    public void testInitTransition(){
        String in = "10";
        String out = "45";
        String aTrack = "2";
        String bTrack = "6";

        MltTransition transition = new MltTransition(in, out, aTrack,  bTrack);

        assertEquals(in, transition.getIn());
        assertEquals(out, transition.getOut());
        assertEquals(aTrack, transition.getATrack());
        assertEquals(bTrack, transition.getBTrack());
    }
}
