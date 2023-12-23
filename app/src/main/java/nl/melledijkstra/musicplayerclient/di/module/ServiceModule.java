package nl.melledijkstra.musicplayerclient.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import nl.melledijkstra.musicplayerclient.di.ServiceContext;
import nl.melledijkstra.musicplayerclient.service.BaseService;

@Module
public class ServiceModule {
    final BaseService mBaseService;

    public ServiceModule(BaseService appPlayerService) {
        mBaseService = appPlayerService;
    }

    @Provides
    @ServiceContext
    Context provideServiceContext() {
        return mBaseService;
    }

    @Provides
    BaseService provideBaseService() {
        return mBaseService;
    }
}
