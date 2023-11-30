package nl.melledijkstra.musicplayerclient.ui.main.album;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class AlbumPresenter<V extends AlbumMPCView> extends BasePresenter<V> implements AlbumMPCPresenter<V> {
    @Inject
    public AlbumPresenter(DataManager dataManager) {
        super(dataManager);
    }
    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
    }
    @Override
    public void retrieveAlbumList() {
        getDataManager().retrieveAlbumList(mView);
    }
}
