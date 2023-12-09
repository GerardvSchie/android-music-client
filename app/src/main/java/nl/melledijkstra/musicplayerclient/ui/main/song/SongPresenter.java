package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.util.Log;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class SongPresenter<V extends SongMPCView> extends BasePresenter<V> implements SongMPCPresenter<V> {
    final static String TAG = "SongPresenter";
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
    public void playSong(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot play song (id=" + songId + ")");
            return;
        }

        getDataManager().playSong(songId);
    }

    @Override
    public void addSongNext(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot addSongNext song (id=" + songId + ")");
            return;
        }

        getDataManager().addSongNext(songId);
    }

    @Override
    public void renameSong(int songId, String newTitle) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot rename song");
            return;
        }

        getDataManager().renameSong(songId, newTitle);
    }

    @Override
    public void deleteSong(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot delete song");
            return;
        }

        getDataManager().deleteSong(songId);
    }

    @Override
    public void moveSong(int songId, int albumId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot move song");
            return;
        }

        getDataManager().moveSong(songId, albumId);
    }
}
