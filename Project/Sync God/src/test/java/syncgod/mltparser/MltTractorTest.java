package syncgod.mltparser;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;

public class MltTractorTest {

    @Test
    public void testInitTractor(){
        String id = "id";
        MltTractor tractor = new MltTractor(id);

        assertEquals(id, tractor.getId());
    }

    @Test
    public void testAddEntry(){
        MltTractor tractor = new MltTractor("");
        MltEntry entry = new MltEntry("2", "9", "7");
        String playlistId = "0";
        tractor.addEntryToPlaylist(entry, playlistId);

        List<MltPlaylist> playlists = tractor.getPlaylists();

        MltPlaylist pl = playlists.get(Integer.parseInt(playlistId));

        assertEquals(1, playlists.size());
        assertEquals(1, playlists.get(0).getEntries().size());
        assertEquals(entry.getProducer(), pl.getEntries().get(0).getProducer());
        assertEquals(entry.getIn(), pl.getEntries().get(0).getIn());
        assertEquals(entry.getOut(), pl.getEntries().get(0).getOut());
    }
}
