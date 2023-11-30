package nl.melledijkstra.musicplayerclient.di.component;

import dagger.Component;
import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.di.module.ActivityModule;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectActivity;
import nl.melledijkstra.musicplayerclient.ui.main.MainActivity;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumFragment;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerFragment;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongFragment;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(ConnectActivity activity);
    void inject(MainActivity activity);
    void inject(SettingsActivity activity);
    void inject(ControllerFragment fragment);
    void inject(AlbumFragment fragment);
    void inject(SongFragment fragment);
}
