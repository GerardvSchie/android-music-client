package nl.melledijkstra.musicplayerclient.ui.connect;

import android.widget.EditText;

import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

@PerActivity
public interface ConnectMPCPresenter<V extends ConnectMPCView> extends MPCPresenter<V> {
    void onConnectClick(String ip);
    void onCreateEditTextIP(EditText editTextIP);
    void connectService(BaseService baseService);
}
