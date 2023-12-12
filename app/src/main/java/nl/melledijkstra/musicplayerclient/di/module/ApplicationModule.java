package nl.melledijkstra.musicplayerclient.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.melledijkstra.musicplayerclient.data.AppDataManager;
import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.prefs.AppPreferencesHelper;
import nl.melledijkstra.musicplayerclient.data.prefs.PreferencesHelper;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.service.AppPlayerService;
import nl.melledijkstra.musicplayerclient.service.BaseService;

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
    BaseService provideBaseService(AppPlayerService appPlayerService) {
        return appPlayerService;
    }

//    @Provides
//    @Singleton
//    BaseService provideBaseService() {
//        Intent intent = AppPlayerService.getStartIntent(provideContext());
//        ComponentName name =  provideApplication().getApplicationContext().startService(intent);
//        provideBaseService().getSystemServiceName(AppPlayerService.class);
//        return provideApplication().getApplicationContext().getSer
//    }

//    @Provides
//    @Singleton
//    BaseService provideBaseService(Retrofit retrofit) {
//        return retrofit.create(AppPlayerService.class);
//    }
}
