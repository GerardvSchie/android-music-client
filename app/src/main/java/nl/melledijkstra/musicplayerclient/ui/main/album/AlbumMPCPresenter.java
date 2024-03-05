package nl.melledijkstra.musicplayerclient.ui.main.album;

import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface AlbumMPCPresenter<V extends AlbumMPCView> extends MPCPresenter<V> {
    boolean isConnected();
    void retrieveAlbumList();
    void connectService(BaseService baseService);
}
