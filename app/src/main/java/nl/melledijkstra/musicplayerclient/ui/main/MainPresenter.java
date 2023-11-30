package nl.melledijkstra.musicplayerclient.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.model.Action;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;

public class MainPresenter<V extends MainMPCView> extends BasePresenter<V> implements MainMPCPresenter<V> {
    static final String TAG = "MainPresenter";
    IntentFilter mBroadcastFilter = new IntentFilter();

    @Inject
    public MainPresenter(DataManager dataManager) {
        super(dataManager);

        mBroadcastFilter.addAction(Action.ACTION_NEXT.toString());
//        mBroadcastFilter.addAction(MelonPlayerService.DISCONNECTED);
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

    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
    }

    @Override
    public void registerReceiver() {
        getDataManager().registerReceiver(bReceiver, mBroadcastFilter);
    }

    @Override
    public void unregisterReceiver() {
        getDataManager().unRegisterReceiver(bReceiver);
    }

    // Broadcast receiver which gets notified of events in the service
    final private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Action action = Action.getAction(intent);
            assert action != null;
            Log.v(TAG, "BROADCAST RECEIVED: " + action);
            switch (action) {
                case ACTION_NEXT:
                    mView.next();
                    break;
            }
        }
    };

    //    final private BroadcastReceiver bReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.v(TAG, "BROADCAST RECEIVED: " + intent.getAction());
//            String action = intent.getAction();
//            assert action != null : "Intent must have an action";
//            if (action.equals(MelonPlayerService.DISCONNECTED)) {
//                // if host disconnects then go to ConnectActivity
//                Intent startConnectActivity = new Intent(MainActivity.this, ConnectActivity.class);
//                startActivity(startConnectActivity);
//                finish();
//            }
//        }
//    };

//    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Action action = Action.getAction(intent);
//            if (action != null) {
//                if (action == ACTION_CLOSE) {
//                    context.stopService(new Intent(ACTION_CLOSE.name()));
////                    String musicPlayerIp = mSettings.getString(AppPreferencesHelper.HOST_IP, null);
////                    int musicPlayerPort = mSettings.getInt(AppPreferencesHelper.HOST_PORT, AppConstants.DEFAULT_PORT);
////                    // if ip is the same, then don't create new channel
////                    if (!previousIp.equals(musicPlayerIp) || channel == null) {
////                    connectBroadcaster(musicPlayerIp, musicPlayerPort);
////                    }
////                    previousIp = musicPlayerIp;
////                    Log.i(TAG, "onReceive: initiating connection with grpc server");
////                    broadcastConnectionResult = true;
////                    channel.getState(true);
//                }
//                return;
//            }
//
//            Message message = Message.getMessage(intent);
//            if (message != null) {
//                if (message == Message.INITIATE_CONNECTION) {
//                    Log.d(TAG, "MESSAGE INITIATE CONNECTION");
//                    context.stopService(new Intent(ACTION_CLOSE.name()));
////                    stopForeground(context.service, 0);
////                    stopSelf();
//                }
//                return;
//            }
//
//            Log.w(TAG, "Didn't receive intent with usage");
//        }
//    };
}
