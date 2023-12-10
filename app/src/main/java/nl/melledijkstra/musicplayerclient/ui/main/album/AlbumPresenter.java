package nl.melledijkstra.musicplayerclient.ui.main.album;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class AlbumPresenter<V extends AlbumMPCView> extends BasePresenter<V> implements AlbumMPCPresenter<V> {
    @Inject
    public AlbumPresenter(DataManager dataManager, BaseService baseService) {
        super(dataManager, baseService);
    }
    @Override
    public boolean isConnected() {
        return mBaseService.isConnected();
    }
    @Override
    public void retrieveAlbumList() {
        mBaseService.retrieveAlbumList(mView);
    }
}
