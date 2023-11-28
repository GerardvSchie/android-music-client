package nl.melledijkstra.musicplayerclient.ui.settings;

import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

@PerActivity
public interface SettingsMPCPresenter<V extends SettingsMPCView> extends MPCPresenter<V> {
}
