package nl.melledijkstra.musicplayerclient.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.melledijkstra.musicplayerclient.data.broadcaster.Broadcaster;
import nl.melledijkstra.musicplayerclient.data.prefs.PreferencesHelper;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;

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
}
