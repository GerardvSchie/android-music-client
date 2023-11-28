package nl.melledijkstra.musicplayerclient.ui.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.EditText;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.model.Message;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class ConnectPresenter<V extends ConnectMPCView> extends BasePresenter<V> implements ConnectMPCPresenter<V> {
    static final String TAG = "ConnectPresenter";
    IntentFilter mBroadcastFilter = new IntentFilter();

    @Inject
    ConnectPresenter(DataManager dataManager) {
        super(dataManager);

        mBroadcastFilter.addAction(Message.READY.toString());
        mBroadcastFilter.addAction(Message.CONNECT_FAILED.toString());
    }

    @Override
    public void onAttach(V mvpView) {
        Log.v(TAG, "onAttach");
        super.onAttach(mvpView);
        registerReceiver();
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach");
        super.onDetach();
        unregisterReceiver();
    }

    public void registerReceiver() {
        getDataManager().registerReceiver(bReceiver, mBroadcastFilter);
    }

    public void unregisterReceiver() {
        getDataManager().unRegisterReceiver(bReceiver);
    }

    public void onConnectClick(String ip) {
        getDataManager().setCurrentHostIP(ip);
        Log.v(TAG, "IP Saved to preferences (" + ip + ")");
        int port = getDataManager().getCurrentHostPort();
        getDataManager().connectBroadcaster(ip, port);
//        LocalBroadcastManager.getInstance(ConnectActivity.this).sendBroadcast(new Intent(MelonPlayerService.INITIATE_CONNECTION));
    }

    @Override
    public void onCreateEditTextIP(EditText editTextIP) {
        editTextIP.setText(getDataManager().getCurrentHostIP());
    }

    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
    }

    // Broadcast receiver which gets notified of events in the service
    final private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = Message.getMessage(intent);
            assert message != null;
            Log.v(TAG, "BROADCAST RECEIVED: " + message);
            switch (message) {
                case READY:
                    mView.openMainActivity();
                    break;
                case CONNECT_FAILED:
                    mView.failConnection();
                    break;
            }
        }
    };
}
