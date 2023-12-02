package nl.melledijkstra.musicplayerclient.ui.main.song;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface SongMPCPresenter<V extends SongMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void retrieveSongList(Album album);
    void play(int songID);
    void addNext(int songId);
    void renameSong(int songId, String newTitle);
    void deleteSong(int songId);
    void moveSong(int songId, int albumId);
}
