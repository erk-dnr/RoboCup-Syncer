package syncgod.mltparser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MltDataTest {

    @Test
    public void TestInitData(){
        String temp = "testtset";
        MltData data = new MltData(temp);

        assertEquals(data.getFileName(), temp);
    }
}
