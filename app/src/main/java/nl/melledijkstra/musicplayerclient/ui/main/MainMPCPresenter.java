package nl.melledijkstra.musicplayerclient.ui.main;

import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

@PerActivity
public interface MainMPCPresenter<V extends MainMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void connect();
    void connectService(BaseService baseService);
}
