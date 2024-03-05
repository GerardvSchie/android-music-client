package nl.melledijkstra.musicplayerclient.ui.main.album;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class AlbumPresenter<V extends AlbumMPCView> extends BasePresenter<V> implements AlbumMPCPresenter<V> {
    BaseService mBaseService;
    @Inject
    public AlbumPresenter(DataManager dataManager) {
        super(dataManager);
    }
    @Override
    public boolean isConnected() {
        return mBaseService.isConnected();
    }
    @Override
    public void retrieveAlbumList() {
        mBaseService.retrieveAlbumList(mView);
    }
    public void connectService(BaseService baseService) {
        this.mBaseService = baseService;
    }
}
