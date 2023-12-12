package nl.melledijkstra.musicplayerclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.di.component.ApplicationComponent;
import nl.melledijkstra.musicplayerclient.di.component.DaggerApplicationComponent;
import nl.melledijkstra.musicplayerclient.di.module.ApplicationModule;
import nl.melledijkstra.musicplayerclient.service.AppPlayerService;
import nl.melledijkstra.musicplayerclient.service.BaseService;

public class App extends Application {
    @Inject
    DataManager mDataManager;
    @Inject
    BaseService mBaseService;
    ApplicationComponent mApplicationComponent;

    // If app is in DEBUG mode then no connection is needed and dummy data is used
    public static boolean DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
        assert mBaseService != null;
        Context context = getApplicationContext();
        assert context != null;
        // This instead of App.this since that does not exist
        Intent intent = AppPlayerService.getStartIntent(this);
        startService(intent);
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
