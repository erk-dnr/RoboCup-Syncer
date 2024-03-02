package syncgod.mltparser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MltPlaylistTest {

    @Test
    public void TestInitPlaylist(){
        String id = "1";
        String hide = "video";

        MltPlaylist pl = new MltPlaylist(id, hide);

        assertEquals(id, pl.getId());
        assertEquals(hide, pl.getWhatIsHidden());
    }

    @Test
    public void TestPlaylistAddEntry(){
        MltPlaylist pl = new MltPlaylist("", "audio");
        MltEntry entry = new MltEntry("2", "9", "7");
        String playlistId = "0";
        pl.addEntry(entry);

        assertEquals(1, pl.getEntries().size());
        assertEquals(1, pl.getEntries().size());
        assertEquals(entry.getProducer(), pl.getEntries().get(0).getProducer());
        assertEquals(entry.getIn(), pl.getEntries().get(0).getIn());
        assertEquals(entry.getOut(), pl.getEntries().get(0).getOut());
    }
}
