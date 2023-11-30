package nl.melledijkstra.musicplayerclient.ui.main.song;

import java.util.ArrayList;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.ui.base.MPCView;

public interface SongMPCView extends MPCView {
    void updateSongList(ArrayList<Song> songArrayList);
    void stopRefresh(boolean hasDelay);
}
