package nl.melledijkstra.musicplayerclient.ui.main.song;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface SongMPCPresenter<V extends SongMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void retrieveSongList(Album album);
}
