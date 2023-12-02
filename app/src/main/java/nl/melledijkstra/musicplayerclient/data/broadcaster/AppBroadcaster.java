package nl.melledijkstra.musicplayerclient.data.broadcaster;

import static nl.melledijkstra.musicplayerclient.utils.NotificationUtils.createNotificationManager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import kotlin.NotImplementedError;
import nl.melledijkstra.musicplayerclient.data.broadcaster.model.Message;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.grpc.AlbumList;
import nl.melledijkstra.musicplayerclient.grpc.DataManagerGrpc;
import nl.melledijkstra.musicplayerclient.grpc.MMPResponse;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatus;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatusRequest;
import nl.melledijkstra.musicplayerclient.grpc.MediaControl;
import nl.melledijkstra.musicplayerclient.grpc.MediaData;
import nl.melledijkstra.musicplayerclient.grpc.MediaType;
import nl.melledijkstra.musicplayerclient.grpc.MoveData;
import nl.melledijkstra.musicplayerclient.grpc.MusicPlayerGrpc;
import nl.melledijkstra.musicplayerclient.grpc.PlaybackControl;
import nl.melledijkstra.musicplayerclient.grpc.PositionControl;
import nl.melledijkstra.musicplayerclient.grpc.RenameData;
import nl.melledijkstra.musicplayerclient.grpc.SongList;
import nl.melledijkstra.musicplayerclient.grpc.VolumeControl;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;
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
    Context context;
    NotificationManager notificationManager;
    LocalBroadcastManager mLocalBroadcastManager;

    @Inject
    public AppBroadcaster(@ApplicationContext Context context) {
        this.context = context;
        notificationManager = createNotificationManager(context);
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
            assert state != null;
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
        if (isConnected()) {
            disconnectBroadcaster();
        }
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
            Log.w(TAG, "Caught exception on disconnect: " + Objects.requireNonNull(e.getMessage()));
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

    @Override
    public void retrieveAlbumList(AlbumMPCView mView) {
        musicPlayerStub.retrieveAlbumList(MediaData.getDefaultInstance(), new StreamObserver<AlbumList>() {
            @Override
            public void onNext(final AlbumList response) {
                Log.i(TAG, "Retrieved albums, count: " + response.getAlbumListCount());
                ArrayList<Album> albums = new ArrayList<>();
                for (nl.melledijkstra.musicplayerclient.grpc.Album album : response.getAlbumListList()) {
                    albums.add(new Album(album));
                }
                mView.updateAlbums(albums);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "grpc onError: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: album list call done");
                mView.stopRefresh(false);
            }
        });
    }

    @Override
    public void retrieveSongList(int albumID, SongMPCView mView) {
        musicPlayerStub.retrieveSongList(MediaData.newBuilder().setId(albumID).build(), new StreamObserver<SongList>() {
            @Override
            public void onNext(final SongList response) {
                Log.i(TAG, "Retrieved songs, count: " + response.getSongListCount());
                ArrayList<Song> songs = new ArrayList<>();
                for (nl.melledijkstra.musicplayerclient.grpc.Song song : response.getSongListList()) {
                    songs.add(new Song(song));
                }
                mView.updateSongList(songs);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "grpc onError: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: retrieving songs done");
                mView.stopRefresh(false);
            }
        });
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

        ConnectivityState state = channel.getState(requestConnection);
        Log.d(TAG, "Current state " + state.toString());
        return state;
    }

    // Once the application is connected with gRPC server this runs
    private void onConnected() {
        Log.v(TAG, "onConnected");
        mLocalBroadcastManager.sendBroadcast(Message.READY.toIntent());
        showNotification();

        musicPlayerStub.registerMMPNotify(MMPStatusRequest.getDefaultInstance(), new StreamObserver<MMPStatus>() {
            @Override
            public void onNext(MMPStatus status) {
                Log.v(TAG, "onNext called");
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
                Log.w(TAG, "registerMMPNotify: " + t.getMessage());
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

    public void play(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot play song (id=" + songId + ")");
            return;
        }

        Log.i(TAG, "Sent play command for songID: " + songId);
        musicPlayerStub.play(MediaControl.newBuilder()
                .setState(MediaControl.State.PLAY)
                .setSongId(songId)
                .build(), defaultMMPResponseStreamObserver);
    }

    public void changeVolume(int newVolume) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot change volume");
            return;
        }

        Log.i(TAG, "Changing volume of song to " + newVolume);
        musicPlayerStub.changeVolume(VolumeControl.newBuilder().setVolumeLevel(newVolume).build(), defaultMMPResponseStreamObserver);
    }

    public void changePosition(int position) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot change position");
            return;
        }

        Log.i(TAG, "Changing position of song to " + position);
        musicPlayerStub.changePosition(PositionControl.newBuilder()
                .setPosition(position).build(), defaultMMPResponseStreamObserver);
    }

    public void previous() {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to previous song");
            return;
        }

        Log.i(TAG, "Go to previous song");
        musicPlayerStub.previous(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
    }

    public void next() {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot go to next song");
            return;
        }

        Log.i(TAG, "Go to next song");
        musicPlayerStub.previous(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
    }

    public void addNext(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot addNext song (id=" + songId + ")");
            return;
        }

        Log.i(TAG, "Sent addNext command for songID: " + songId);
        musicPlayerStub.addNext(MediaData.newBuilder()
                .setType(MediaType.SONG)
                .setId(songId).build(), defaultMMPResponseStreamObserver);
    }

    public void addToQueue(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot addToQueue song (id=" + songId + ")");
            return;
        }

        // TODO: Implement
        throw new NotImplementedError();
    }

    public void renameSong(int songId, String newTitle) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot rename song");
            return;
        }

        Log.i(TAG, "Rename song id=" + songId + " to '" + newTitle + "'");
        dataManagerStub.renameSong(RenameData.newBuilder()
                .setId(songId)
                .setNewTitle(newTitle).build(), defaultMMPResponseStreamObserver);
    }

    public void deleteSong(int songId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot delete song");
            return;
        }

        Log.i(TAG, "Deleting song id=" + songId);
        dataManagerStub.deleteSong(MediaData.newBuilder()
                .setId(songId).build(), defaultMMPResponseStreamObserver);
    }

    public void moveSong(int songId, int albumId) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected: Cannot move song");
            return;
        }

        Log.i(TAG, "Move song id=" + songId + " to albumid=" + albumId);
        dataManagerStub.moveSong(MoveData.newBuilder()
                .setSongId(songId)
                .setAlbumId(albumId)
                .build(), defaultMMPResponseStreamObserver);
    }
}
