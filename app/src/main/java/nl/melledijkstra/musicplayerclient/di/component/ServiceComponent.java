package nl.melledijkstra.musicplayerclient.di.component;

import dagger.Component;
import nl.melledijkstra.musicplayerclient.di.PerService;
import nl.melledijkstra.musicplayerclient.di.module.ServiceModule;
import nl.melledijkstra.musicplayerclient.service.BaseService;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(BaseService service);
}
