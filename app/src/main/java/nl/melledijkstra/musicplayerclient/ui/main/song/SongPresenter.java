package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.util.Log;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class SongPresenter<V extends SongMPCView> extends BasePresenter<V> implements SongMPCPresenter<V> {
    final static String TAG = "SongPresenter";
    @Inject
    public SongPresenter(DataManager dataManager, BaseService baseService) {
        super(dataManager, baseService);
    }
    @Override
    public boolean isConnected() {
        return mBaseService.isConnected();
    }
    @Override
    public void retrieveSongList(Album album) {
        mBaseService.retrieveSongList((int)album.ID, mView);
    }
    @Override
    public void playSong(int songId) {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot play song (id=" + songId + ")");
            return;
        }

        mBaseService.playSong(songId);
    }

    @Override
    public void addSongNext(int songId) {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot addSongNext song (id=" + songId + ")");
            return;
        }

        mBaseService.addSongNext(songId);
    }

    @Override
    public void renameSong(int songId, String newTitle) {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot rename song");
            return;
        }

        mBaseService.renameSong(songId, newTitle);
    }

    @Override
    public void deleteSong(int songId) {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot delete song");
            return;
        }

        mBaseService.deleteSong(songId);
    }

    @Override
    public void moveSong(int songId, int albumId) {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot move song");
            return;
        }

        mBaseService.moveSong(songId, albumId);
    }
}
