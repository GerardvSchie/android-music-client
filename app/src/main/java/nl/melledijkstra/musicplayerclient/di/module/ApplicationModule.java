package nl.melledijkstra.musicplayerclient.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.melledijkstra.musicplayerclient.data.AppDataManager;
import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.AppBroadcaster;
import nl.melledijkstra.musicplayerclient.data.broadcaster.Broadcaster;
import nl.melledijkstra.musicplayerclient.data.prefs.AppPreferencesHelper;
import nl.melledijkstra.musicplayerclient.data.prefs.PreferencesHelper;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;

@Module
public class ApplicationModule {
    final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    Broadcaster provideBroadcaster(AppBroadcaster appBroadcaster) {
        return appBroadcaster;
    }
}
