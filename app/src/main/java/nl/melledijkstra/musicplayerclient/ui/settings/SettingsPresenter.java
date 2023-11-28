package nl.melledijkstra.musicplayerclient.ui.settings;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class SettingsPresenter<V extends SettingsMPCView> extends BasePresenter<V> implements SettingsMPCPresenter<V> {
    @Inject
    public SettingsPresenter(DataManager dataManager) {
        super(dataManager);
    }
}
