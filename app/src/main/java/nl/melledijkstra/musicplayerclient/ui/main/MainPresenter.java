package nl.melledijkstra.musicplayerclient.ui.main;

import android.util.Log;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class MainPresenter<V extends MainMPCView> extends BasePresenter<V> implements MainMPCPresenter<V> {
    static final String TAG = "MainPresenter";

    @Inject
    public MainPresenter(DataManager dataManager) {
        super(dataManager);
    }

    public boolean isConnected() {
        return getDataManager().isConnected();
    }

    @Override
    public void connect() {
        if (getDataManager().isConnected()) {
            Log.w(TAG, "Try to connect whilst already connected, do nothing");
            return;
        }
        getDataManager().connectBroadcaster(getDataManager().getCurrentHostIP(), getDataManager().getCurrentHostPort());
    }
}
