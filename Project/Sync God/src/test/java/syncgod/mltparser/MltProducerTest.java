package syncgod.mltparser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MltProducerTest {

    @Test
    public void testProducer(){
        String id = "id";
        String property = "propertylol";

        MltProducer producer = new MltProducer(id, property);

        assertEquals(id, producer.getId());
        assertEquals(property, producer.getProperty());
    }
}
