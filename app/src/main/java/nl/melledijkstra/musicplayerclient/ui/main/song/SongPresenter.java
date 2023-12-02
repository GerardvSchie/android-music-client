package nl.melledijkstra.musicplayerclient.ui.main.song;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class SongPresenter<V extends SongMPCView> extends BasePresenter<V> implements SongMPCPresenter<V> {
    @Inject
    public SongPresenter(DataManager dataManager) {
        super(dataManager);
    }
    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
    }
    @Override
    public void retrieveSongList(Album album) {
        getDataManager().retrieveSongList((int)album.ID, mView);
    }
    @Override
    public void play(int songID) {
        getDataManager().play(songID);
    }

    @Override
    public void addNext(int songId) {
        getDataManager().addNext(songId);
    }

    @Override
    public void renameSong(int songId, String newTitle) {
        getDataManager().renameSong(songId, newTitle);
    }

    @Override
    public void deleteSong(int songId) {
        getDataManager().deleteSong(songId);
    }

    @Override
    public void moveSong(int songId, int albumId) {
        getDataManager().moveSong(songId, albumId);
    }
}
