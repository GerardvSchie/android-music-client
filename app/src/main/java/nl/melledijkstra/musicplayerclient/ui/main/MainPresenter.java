package nl.melledijkstra.musicplayerclient.ui.main;

import android.util.Log;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class MainPresenter<V extends MainMPCView> extends BasePresenter<V> implements MainMPCPresenter<V> {
    static final String TAG = "MainPresenter";
    @Inject
    public MainPresenter(DataManager dataManager, BaseService baseService) {
        super(dataManager, baseService);
    }

    public boolean isConnected() {
        return mBaseService.isConnected();
    }

    @Override
    public void connect() {
        if (mBaseService.isConnected()) {
            Log.w(TAG, "Try to connect whilst already connected, do nothing");
            return;
        }

        mBaseService.connectPlayerServer(getDataManager().getCurrentHostIP(), getDataManager().getCurrentHostPort());
    }
}
