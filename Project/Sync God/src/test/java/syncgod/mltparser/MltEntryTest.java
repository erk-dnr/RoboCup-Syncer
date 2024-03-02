package syncgod.mltparser;

import static org.junit.Assert.*;

import org.junit.Test;
public class MltEntryTest {

    @Test
    public void testEntry(){
        String producerId = "testProd";
        String in = "1";
        String out = "5";

        MltEntry entry = new MltEntry(producerId,in,out);

        assertEquals(producerId, entry.getProducer());
        assertEquals(in, entry.getIn());
        assertEquals(out, entry.getOut());
       }
}
