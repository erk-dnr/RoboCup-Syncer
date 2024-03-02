package syncgod.mltparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import syncgod.track.TrackBox;
import syncgod.track.TrackMarker;
import syncgod.videoplayer.VideoPlayer;

public class MltData {

    private String fileName;
    private List<MltProducer> producer;
    private List<MltTractor> tractors;


    private void init(String fileName) {
        this.fileName = fileName;
        this.producer = new ArrayList<>();
        this.tractors = new ArrayList<>();
    }

    public MltData(String fileName) {
        init(fileName);
    }

    public MltData(String fileName, List<VideoPlayer> player, List<TrackBox> tracks) {
        init(fileName);
        fillProducers(player);
        fillTractors(tracks);
    }

    private void fillProducers(List<VideoPlayer> player) {
        for (int i = 0; i < player.size(); i++) {
            producer.add(new MltProducer("video" + i, player.get(i).getPath(),
                                         String.valueOf(player.get(i).getOffset())));
        }
    }

    private final Comparator<TrackMarker> byStartFrame =
            Comparator.comparingLong(TrackMarker::getFrameStart);

    private final Comparator<MltEntry> byIn =
            Comparator.comparingLong(MltEntry::getLongIn);

    private void fillTractors(List<TrackBox> tracks) {
        MltTractor tractor = new MltTractor("tractor0");
        List<TrackMarker> trackMarkers;
        MltPlaylist playlist = new MltPlaylist("playlist0", "audio");

        for (int i = 0; i < tracks.size(); i++) {
            trackMarkers = tracks.get(i).getMarkers();
            trackMarkers.sort(byStartFrame);
            for (TrackMarker trackMarker : trackMarkers) {
                playlist.addEntry(new MltEntry("video" + i,
                                               trackMarker.getFrameStart().toString(),
                                               trackMarker.getFrameEnd().toString()
                ));
            }
        }
        playlist.getEntries().sort(byIn);
        tractor.addPlaylist(playlist);

        tractors.add(tractor);
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public List<MltProducer> getProducers() {
        return producer;
    }

    public void addProducer(MltProducer producer) {
        getProducers().add(producer);
    }

    public List<MltTractor> getTractors() {
        return tractors;
    }

    public void addTractor(MltTractor tractor) {
        tractors.add(tractor);
    }

    public String getFileName() {
        return fileName;
    }
}
