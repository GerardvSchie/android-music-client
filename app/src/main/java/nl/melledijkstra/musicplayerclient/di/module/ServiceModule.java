package nl.melledijkstra.musicplayerclient.di.module;

import android.app.Service;

import dagger.Module;

@Module
public class ServiceModule {
    final Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }
}
