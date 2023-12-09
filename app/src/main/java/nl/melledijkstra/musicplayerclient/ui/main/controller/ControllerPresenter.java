package nl.melledijkstra.musicplayerclient.ui.main.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.TimerTask;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.data.broadcaster.model.Action;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;
import nl.melledijkstra.musicplayerclient.utils.PlayerTimer;

public class ControllerPresenter<V extends ControllerMPCView> extends BasePresenter<V> implements ControllerMPCPresenter<V> {
    static final String TAG = "ControllerPresenter";
    PlayerTimer playerTimer = new PlayerTimer();
    IntentFilter mBroadcastFilter = new IntentFilter();

    @Inject
    public ControllerPresenter(DataManager dataManager) {
        super(dataManager);
        mBroadcastFilter.addAction(Action.ACTION_NEXT.toString());
//        mBroadcastFilter.addAction(MelonPlayerService.DISCONNECTED);
    }

    @Override
    public boolean isConnected() {
        return getDataManager().isConnected();
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
    public void registerReceiver() {
        getDataManager().registerReceiver(bReceiver, mBroadcastFilter);
    }

    @Override
    public void unregisterReceiver() {
        getDataManager().unRegisterReceiver(bReceiver);
    }

    public void registerStateChangeListener(AppPlayer.StateUpdateListener listener) {
        getDataManager().registerStateChangeListener(listener);
    }

    public void unRegisterStateChangeListener(AppPlayer.StateUpdateListener listener) {
        getDataManager().unRegisterStateChangeListener(listener);
    }

    @Override
    public void playPause() {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot play/pause");
            return;
        }

        getDataManager().playPause();
        mView.updatePlayPause(getDataManager().getAppPlayer());
    }

    @Override
    public void previous() {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to previous song");
            return;
        }

        getDataManager().previous();
    }

    @Override
    public void next() {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to next song");
            return;
        }

        getDataManager().next();
    }

    @Override
    public void changePosition(int position) {
        getDataManager().changePosition(position);
    }

    @Override
    public void changeVolume(int volume) {
        getDataManager().changeVolume(volume);
    }

    @Override
    public AppPlayer appPlayer() {
        return getDataManager().getAppPlayer();
    }

    @Override
    public void startTimer() {
        AppPlayer appPlayer = getDataManager().getAppPlayer();
        playerTimer.startTimer(appPlayer, new TimerTask() {
            @Override
            public void run() {
                mView.updateProgressBar(appPlayer, playerTimer);
            }
        });
    }

    @Override
    public void stopTimer() {
        playerTimer.stopTimer();
    }

    @Override
    public void resetTimer() {
        AppPlayer appPlayer = getDataManager().getAppPlayer();
        playerTimer.resetTimer(appPlayer, new TimerTask() {
            @Override
            public void run() {
                mView.updateProgressBar(appPlayer, playerTimer);
            }
        });
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
                    getDataManager().retrieveNewStatus();
//                    mView.next();
                    break;
            }
        }
    };
//            if (action.equals(MelonPlayerService.DISCONNECTED)) {
//                // if host disconnects then go to ConnectActivity
//                Intent startConnectActivity = new Intent(MainActivity.this, ConnectActivity.class);
//                startActivity(startConnectActivity);
//                finish();
//            }
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
