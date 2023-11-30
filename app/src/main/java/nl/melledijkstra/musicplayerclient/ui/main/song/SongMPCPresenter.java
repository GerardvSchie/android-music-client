package nl.melledijkstra.musicplayerclient.ui.main.song;

import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface SongMPCPresenter<V extends SongMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void retrieveSongList(int albumID);
}
