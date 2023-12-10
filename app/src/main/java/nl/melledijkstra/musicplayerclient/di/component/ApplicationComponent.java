package nl.melledijkstra.musicplayerclient.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.di.module.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(App app);
    @ApplicationContext
    Context context();
    Application application();
    DataManager getDataManager();
}
