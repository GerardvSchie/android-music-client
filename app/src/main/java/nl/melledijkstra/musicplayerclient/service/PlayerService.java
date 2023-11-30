package nl.melledijkstra.musicplayerclient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.di.component.DaggerServiceComponent;
import nl.melledijkstra.musicplayerclient.di.component.ServiceComponent;

// This service interacts with the Player server
public class PlayerService extends Service implements AppPlayer.StateUpdateListener {
    static final String TAG = "MelonPlayerService";
    @Inject
    DataManager mDataManager;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, PlayerService.class);
        context.startService(starter);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, PlayerService.class));
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        ServiceComponent component = DaggerServiceComponent.builder()
                .applicationComponent(((App)getApplication()).getComponent())
                .build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Serviced started");
        // if this service's process is killed while it is started (after returning from onStartCommand(Intent, int, int)),
        // and there are no new start intents to deliver to it, then take the service out of the started state
        // and don't recreate until a future explicit call to Context.startService(Intent)
        return START_NOT_STICKY;
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

    @Override
    public void MelonPlayerStateUpdated() {
        //showNotification();
    }

    // This binder gives the service to the binding object
    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
