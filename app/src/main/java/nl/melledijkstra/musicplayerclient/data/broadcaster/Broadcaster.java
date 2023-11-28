package nl.melledijkstra.musicplayerclient.data.broadcaster;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public interface Broadcaster {
    void connectBroadcaster(String ip, int port);
    boolean isConnected();
    void disconnectBroadcaster();
    void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter);
    void unRegisterReceiver(BroadcastReceiver broadcastReceiver);
}
