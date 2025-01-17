package nl.melledijkstra.musicplayerclient.ui.main.song;

import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface SongMPCPresenter<V extends SongMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void retrieveSongList(Album album);
    void playSong(int songId);
    void addSongNext(int songId);
    void renameSong(int songId, String newTitle);
    void deleteSong(int songId);
    void moveSong(int songId, int albumId);
    void connectService(BaseService baseService);
}
