package nl.melledijkstra.musicplayerclient.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.melledijkstra.musicplayerclient.data.broadcaster.Broadcaster;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.data.prefs.PreferencesHelper;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;

@Singleton
public class AppDataManager implements DataManager {
    public final Context mContext;
    public final PreferencesHelper mPreferencesHelper;
    public final Broadcaster mBroadcaster;

    @Inject
    public AppDataManager(@ApplicationContext Context context, PreferencesHelper preferencesHelper, Broadcaster broadcaster) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mBroadcaster = broadcaster;
    }

    @Override
    public String getCurrentHostIP() {
        return mPreferencesHelper.getCurrentHostIP();
    }

    @Override
    public void setCurrentHostIP(String ip) {
        mPreferencesHelper.setCurrentHostIP(ip);
    }

    @Override
    public int getCurrentHostPort() {
        return mPreferencesHelper.getCurrentHostPort();
    }

    @Override
    public void setCurrentHostPort(int port) {
        mPreferencesHelper.setCurrentHostPort(port);
    }

    @Override
    public boolean getDebug() {
        return mPreferencesHelper.getDebug();
    }

    @Override
    public void setDebug(boolean debug) {
        mPreferencesHelper.setDebug(debug);
    }

    @Override
    public void connectBroadcaster(String ip, int port) {
        mBroadcaster.connectBroadcaster(ip, port);
    }

    @Override
    public boolean isConnected() {
        return mBroadcaster.isConnected();
    }

    @Override
    public void disconnectBroadcaster() {
        mBroadcaster.disconnectBroadcaster();
    }

    @Override
    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        mBroadcaster.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        mBroadcaster.unRegisterReceiver(broadcastReceiver);
    }

    @Override
    public void registerStateChangeListener(AppPlayer.StateUpdateListener listener) {
        mBroadcaster.registerStateChangeListener(listener);
    }

    @Override
    public void unRegisterStateChangeListener(AppPlayer.StateUpdateListener listener) {
        mBroadcaster.unRegisterStateChangeListener(listener);
    }

    @Override
    public void retrieveAlbumList(AlbumMPCView albumMPCView) {
        mBroadcaster.retrieveAlbumList(albumMPCView);
    }

    @Override
    public void retrieveSongList(int albumID, SongMPCView songMPCView) {
        mBroadcaster.retrieveSongList(albumID, songMPCView);
    }

    @Override
    public void retrieveNewStatus() {
        mBroadcaster.retrieveNewStatus();
    }

    @Override
    public AppPlayer getAppPlayer() {
        return mBroadcaster.getAppPlayer();
    }

    @Override
    public void playSong(int songId) {
        mBroadcaster.playSong(songId);
    }

    @Override
    public void changeVolume(int newVolume) {
        mBroadcaster.changeVolume(newVolume);
    }

    @Override
    public void changePosition(int position) {
        mBroadcaster.changePosition(position);
    }

    @Override
    public void playPause() {
        mBroadcaster.playPause();
    }

    @Override
    public void previous() {
        mBroadcaster.previous();
    }

    @Override
    public void next() {
        mBroadcaster.next();
    }

    @Override
    public void addSongNext(int songId) {
        mBroadcaster.addSongNext(songId);
    }

    @Override
    public void renameSong(int songId, String newTitle) {
        mBroadcaster.renameSong(songId, newTitle);
    }

    @Override
    public void deleteSong(int songId) {
        mBroadcaster.deleteSong(songId);
    }

    @Override
    public void moveSong(int songId, int albumId) {
        mBroadcaster.moveSong(songId, albumId);
    }
}
