package nl.melledijkstra.musicplayerclient;

import android.app.Application;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.di.component.ApplicationComponent;
import nl.melledijkstra.musicplayerclient.di.component.DaggerApplicationComponent;
import nl.melledijkstra.musicplayerclient.di.module.ApplicationModule;

public class App extends Application {
    @Inject
    DataManager mDataManager;
    ApplicationComponent mApplicationComponent;

    // If app is in DEBUG mode then no connection is needed and dummy data is used
    public static boolean DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
