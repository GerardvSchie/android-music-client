package nl.melledijkstra.musicplayerclient.data.broadcaster;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import nl.melledijkstra.musicplayerclient.data.broadcaster.model.Message;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.grpc.DataManagerGrpc;
import nl.melledijkstra.musicplayerclient.grpc.MMPResponse;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatus;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatusRequest;
import nl.melledijkstra.musicplayerclient.grpc.MusicPlayerGrpc;
import nl.melledijkstra.musicplayerclient.utils.NotificationUtils;

@Singleton
public class AppBroadcaster implements Broadcaster {
    static final String TAG = "AppBroadcaster";

    // gRPC: Stubs to initiate calls to server
    public MusicPlayerGrpc.MusicPlayerStub musicPlayerStub;
    public DataManagerGrpc.DataManagerStub dataManagerStub;
    public AppPlayer appPlayer = AppPlayer.getInstance();

    // gRPC: Managed channel result should be broadcast
    ManagedChannel channel;
    volatile boolean broadcastConnectionResult = true;
    NotificationManager notificationManager;
    Context context;
    LocalBroadcastManager mLocalBroadcastManager;

    @Inject
    public AppBroadcaster(@ApplicationContext Context context) {
        this.context = context;
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    // Runnable passed to grpc channel to run when state is changed
    final Runnable grpcStateChangeListener = new Runnable() {
        @Override
        public void run() {
            if (channel == null) {
                Log.w(TAG, "Channel is null, can't run");
                return;
            }

            ConnectivityState state = getState(false);
            Log.d(TAG, "STATE CHANGED: " + state);
            channel.notifyWhenStateChanged(state, grpcStateChangeListener);
            switch (state) {
                case READY:
                    onConnected();
                    break;
                case CONNECTING:
                    break;
                case IDLE:
                case TRANSIENT_FAILURE:
                default:
                    if (broadcastConnectionResult) {
                        Intent intent = Message.CONNECT_FAILED.toIntent();
                        intent.putExtra("state", state.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        Log.i(TAG, "CONNECT_FAILED broadcast sent");
                        broadcastConnectionResult = false;
                    }
                    break;
                case SHUTDOWN:
                    disconnectBroadcaster();
                    break;
            }
        }
    };

//    public void onStartCommand(Intent intent) {
//        Action action = Action.getAction(intent);
//        assert action != null : "Start command action is null";
//        Log.v(TAG, String.format("onStartCommand: {action: %s}", action));
//
//        // TODO: Add other playback actions
//        switch (action) {
//            case ACTION_PLAY_PAUSE:
//                musicPlayerStub.play(MediaControl.newBuilder()
//                        .setState(MediaControl.State.PAUSE)
//                        .build(), defaultMMPResponseStreamObserver);
//                break;
//            case ACTION_PREV:
//                musicPlayerStub.previous(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
//                break;
//            case ACTION_NEXT:
//                musicPlayerStub.next(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
//                break;
//        }
//    }

    final StreamObserver<MMPStatus> statusStreamObserver = new StreamObserver<MMPStatus>() {
        @Override
        public void onNext(MMPStatus newState) {
            Log.d(TAG, String.format("onNext in streamObserver with state %s", newState));
            appPlayer.setState(newState);
            if (newState.getState() == MMPStatus.State.PLAYING) {
//                context.startForeground(NOTIFICATION_ID, musicPlaybackNotification);
            } else {
//                context.stopForeground(false);
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
        }
    };

    // Default response stream observer
    public StreamObserver<MMPResponse> defaultMMPResponseStreamObserver = new StreamObserver<MMPResponse>() {
        @Override
        public void onNext(MMPResponse response) {
            // check if an error occurred at server
            if (response.getResult() == MMPResponse.Result.ERROR
                    && !response.getError().isEmpty()) {
                Log.e(TAG, "GRPC SERVER: " + response.getError());
            }
            if (!response.getMessage().isEmpty() && context != null) {
                // TODO: create object out of StreamObserver so it accepts a context which can be used to create Toast
                //Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {}
    };

    @Override
    public void connectBroadcaster(String ip, int port) {
        disconnectBroadcaster();
        // Create communication with MusicPlayerServer
        Log.i(TAG, "Connecting to " + ip + ":" + port);
        channel = ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext(true)
                .build();
        ConnectivityState state = getState(true);
        channel.notifyWhenStateChanged(state, grpcStateChangeListener);
        musicPlayerStub = MusicPlayerGrpc.newStub(channel);
        dataManagerStub = DataManagerGrpc.newStub(channel);
    }

    public void disconnectBroadcaster() {
        if (channel == null) {
            return;
        }

        Log.i(TAG, "disconnectBroadcaster: shutting down grpc client");
        try {
            channel.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        } finally {
            channel = null;
            musicPlayerStub = null;
            dataManagerStub = null;
        }

        NotificationUtils.removeNotification(notificationManager);
        LocalBroadcastManager.getInstance(context).sendBroadcast(Message.DISCONNECTED.toIntent());
        Log.i(TAG, "disconnectBroadcaster: grpc client terminated");
    }

    @Override
    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        Log.d(TAG, "Registering receiver...");
        assert broadcastReceiver != null && intentFilter != null;
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        Log.d(TAG, "Unregistering receiver...");
        assert broadcastReceiver != null;
        mLocalBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    // Forcibly get the newest melon player status
    public void retrieveNewStatus() {
        musicPlayerStub.retrieveMMPStatus(MMPStatusRequest.getDefaultInstance(), statusStreamObserver);
    }

    // Check if device is still connected with gRPC server
    public boolean isConnected() {
        return Objects.equals(getState(false), ConnectivityState.READY);
    }

    private ConnectivityState getState(boolean requestConnection) {
        if (channel == null) {
            Log.w(TAG, "Getting state whilst it is null");
            return null;
        }
//        assert channel != null : "Cannot retrieve state if channel is null";
        ConnectivityState state = channel.getState(requestConnection);
        Log.d(TAG, "Current state " + state.toString());
        return state;
    }

    // Once the application is connected with gRPC server this runs
    private void onConnected() {
        Log.v(TAG, "onConnected");
        mLocalBroadcastManager.sendBroadcast(Message.READY.toIntent());
        Log.d(TAG, "onConnected: READY broadcast sent");
        showNotification();

        musicPlayerStub.registerMMPNotify(MMPStatusRequest.getDefaultInstance(), new StreamObserver<MMPStatus>() {
            @Override
            public void onNext(MMPStatus status) {
                Log.d(TAG, "onNext called");
                appPlayer.setState(status);
                // TODO: update the player and that should update notification
                if (status.getState() == MMPStatus.State.PLAYING) {
//                    startForeground(NOTIFICATION_ID, musicPlaybackNotification);
                } else {
//                    stopForeground(false);
                }
                showNotification();
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG, "registerMMPNotify: " + t.getMessage());
                disconnectBroadcaster();
            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: Status call done");
            }
        });
    }

    private void showNotification() {
        NotificationUtils.showNotification(context, notificationManager, appPlayer);
    }
}
