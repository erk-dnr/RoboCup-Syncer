package syncgod.mltparser;

import java.util.ArrayList;
import java.util.List;

public class MltTractor {

    private String id;
    private List<MltPlaylist> playlists;
    private List<MltTransition> transitions;

    public MltTractor(String id) {
        this.id = id;
        playlists = new ArrayList<>();
        transitions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<MltPlaylist> getPlaylists() {
        return playlists;
    }

    public List<MltTransition> getTransitions() {
        return transitions;
    }

    public void addEntryToPlaylist(MltEntry entry, String playlistId) {
        for (MltPlaylist playlist : playlists) {
            if (playlist.getId().equals(playlistId)) {
                playlist.addEntry(entry);
                return;
            }
        }
        MltPlaylist playlist = new MltPlaylist(playlistId,"");
        playlist.addEntry(entry);
        playlists.add(playlist);
    }

    public void addPlaylist(MltPlaylist playlist) {
        playlists.add(playlist);
    }

    public void addTransition(MltTransition transition) {
        transitions.add(transition);
    }
}
