package nl.melledijkstra.musicplayerclient.service;

import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

// This service interacts with the Player server
public class AppPlayerService extends BaseService implements PlayerService {
    static final String TAG = "AppService";

    NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Serviced started");
        return START_NOT_STICKY;
//        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public void PlayerStateUpdated() {
        showNotification();
    }

    private void showNotification() {
//        NotificationUtils.showNotification(getBaseContext(), notificationManager, mDataManager.getAppPlayer());
    }
}
