package nl.melledijkstra.musicplayerclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public abstract class BaseService extends Service implements PlayerService {
    static final String TAG = "BaseService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Serviced started");
//        return START_NOT_STICKY;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
