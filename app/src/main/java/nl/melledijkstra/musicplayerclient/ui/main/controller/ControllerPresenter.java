package nl.melledijkstra.musicplayerclient.ui.main.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.TimerTask;

import javax.inject.Inject;

import nl.melledijkstra.musicplayerclient.data.DataManager;
import nl.melledijkstra.musicplayerclient.service.BaseService;
import nl.melledijkstra.musicplayerclient.service.model.Action;
import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.ui.base.BasePresenter;
import nl.melledijkstra.musicplayerclient.utils.PlayerTimer;

public class ControllerPresenter<V extends ControllerMPCView> extends BasePresenter<V> implements ControllerMPCPresenter<V> {
    static final String TAG = "ControllerPresenter";
    BaseService mBaseService;
    PlayerTimer playerTimer = new PlayerTimer();
    @Inject
    public ControllerPresenter(DataManager dataManager) {
        super(dataManager);
    }

    public void connectService(BaseService baseService) {
        this.mBaseService = baseService;
        IntentFilter mBroadcastFilter = new IntentFilter();
        mBroadcastFilter.addAction(Action.ACTION_NEXT.toString());
//        mBroadcastFilter.addAction(MelonPlayerService.DISCONNECTED);
        mBaseService.registerReceiver(bReceiver, mBroadcastFilter);
    }

    @Override
    public void playPause() {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot play/pause");
            return;
        }

        mBaseService.playPause();
        mView.updatePlayPause(mBaseService.getAppPlayer());
    }

    @Override
    public void previous() {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to previous song");
            return;
        }

        mBaseService.previous();
    }

    @Override
    public void next() {
        if (!mBaseService.isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to next song");
            return;
        }

        mBaseService.next();
    }

    @Override
    public void changePosition(int position) {
        mBaseService.changePosition(position);
    }

    @Override
    public void changeVolume(int volume) {
        mBaseService.changeVolume(volume);
    }

    @Override
    public AppPlayer appPlayer() {
        return mBaseService.getAppPlayer();
    }

    @Override
    public void startTimer() {
        AppPlayer appPlayer = mBaseService.getAppPlayer();
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
        AppPlayer appPlayer = mBaseService.getAppPlayer();
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
                    mBaseService.retrieveNewStatus();
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
