package nl.melledijkstra.musicplayerclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.di.component.DaggerServiceComponent;
import nl.melledijkstra.musicplayerclient.di.component.ServiceComponent;
import nl.melledijkstra.musicplayerclient.di.module.ServiceModule;

public abstract class BaseService extends Service implements PlayerService {
    static final String TAG = "BaseService";

    ServiceComponent mServiceComponent;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        mServiceComponent = DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .applicationComponent(((App) getApplication()).getComponent())
                .build();

        mServiceComponent.inject(this);
    }

    public ServiceComponent getServiceComponent() {
        assert mServiceComponent != null : "Service must be filled";
        return mServiceComponent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
