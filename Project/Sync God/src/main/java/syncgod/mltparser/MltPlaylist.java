package syncgod.mltparser;

import java.util.ArrayList;
import java.util.List;

public class MltPlaylist {

    private String id;
    private String hide;
    private List<MltEntry> entries;
    private List<MltTransition> transitions;

    public MltPlaylist(String id, String hide) {
        this.id = id;
        this.hide = hide;
        this.entries = new ArrayList<>();
    }

    public String getWhatIsHidden() {
        return hide;
    }

    public List<MltEntry> getEntries() {
        return entries;
    }

    public String getId() {
        return id;
    }

    public void addEntry(MltEntry entry) {
        entries.add(entry);
    }
}
