package nl.melledijkstra.musicplayerclient.service;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(AppPlayerService appPlayerService);
}