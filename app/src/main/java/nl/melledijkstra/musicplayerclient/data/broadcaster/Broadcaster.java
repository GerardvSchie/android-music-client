package nl.melledijkstra.musicplayerclient.data.broadcaster;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;

public interface Broadcaster {
    void connectBroadcaster(String ip, int port);
    boolean isConnected();
    void disconnectBroadcaster();
    void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter);
    void unRegisterReceiver(BroadcastReceiver broadcastReceiver);
    void retrieveAlbumList(AlbumMPCView albumMPCView);
    void retrieveSongList(int albumID, SongMPCView songMPCView);
}
