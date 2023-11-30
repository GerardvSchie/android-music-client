package nl.melledijkstra.musicplayerclient.ui.main.song;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class SongPresenter<V extends SongMPCView> extends BasePresenter<V> implements SongMPCPresenter<V> {
    @Inject
    public SongPresenter(DataManager dataManager) {
        super(dataManager);
    }
    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
    }
    @Override
    public void retrieveSongList(int albumID) {
        getDataManager().retrieveSongList(albumID, mView);
    }
}
