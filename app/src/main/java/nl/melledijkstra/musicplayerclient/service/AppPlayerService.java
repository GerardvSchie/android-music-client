package nl.melledijkstra.musicplayerclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
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
import nl.melledijkstra.musicplayerclient.service.model.Message;
import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.service.player.model.Song;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;

// This service interacts with the Player server
public class AppPlayerService extends BaseService implements PlayerService {
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public AppPlayerService getService() {
            return AppPlayerService.this;
        }
    }

    static final String TAG = "AppService";

    // gRPC: Stubs to initiate calls to server
    public MusicPlayerGrpc.MusicPlayerStub musicPlayerStub;
    public DataManagerGrpc.DataManagerStub dataManagerStub;
    ManagedChannel channel;

    // gRPC: Managed channel result should be broadcast
    LocalBroadcastManager mLocalBroadcastManager;
    AppPlayer appPlayer;
    PlayerNotificationManager playerNotificationManager;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, AppPlayerService.class);
    }

    @Inject
    public AppPlayerService(@ApplicationContext Context context) {
        Log.e(TAG, "CONSTRUCTOR");
        assert context != null;
        playerNotificationManager = new PlayerNotificationManager(context);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        appPlayer = new AppPlayer(this);
        startForeground(PlayerNotificationManager.NOTIFICATION_ID, playerNotificationManager.createNotification(appPlayer));
    }

//    Context context;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return binder;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppPlayer getAppPlayer() {
        return appPlayer;
    }

    @Override
    public void PlayerStateUpdated() {
        playerNotificationManager.showNotification(appPlayer);
    }

    //region GRPC Server
    // Runnable passed to grpc channel to run when state is changed
    public void checkChannelConnected() {
        assert channel != null : "Channel is null";
    }

    final Runnable grpcStateChangeListener = new Runnable() {
        @Override
        public void run() {
            checkChannelConnected();
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
                    Intent intent = Message.CONNECT_FAILED.toIntent();
                    intent.putExtra("state", state.toString());
                    mLocalBroadcastManager.sendBroadcast(intent);
                    Log.i(TAG, "CONNECT_FAILED broadcast sent");
                    break;
                case SHUTDOWN:
                    disconnectPlayerServer();
                    break;
            }
        }
    };

    public boolean isConnected() {
        return Objects.equals(getState(false), ConnectivityState.READY);
    }

    private void onConnected() {
        Log.v(TAG, "onConnected");
        mLocalBroadcastManager.sendBroadcast(Message.READY.toIntent());
        musicPlayerStub.registerMMPNotify(MMPStatusRequest.getDefaultInstance(), statusStreamObserver);
    }

    private ConnectivityState getState(boolean requestConnection) {
        checkChannelConnected();
        ConnectivityState state = channel.getState(requestConnection);
        Log.d(TAG, "Current state " + state.toString());
        return state;
    }

    public void connectPlayerServer(String ip, int port) {
        if (channel != null) {
            disconnectPlayerServer();
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

    public void disconnectPlayerServer() {
        if (channel == null) {
            return;
        }

        Log.i(TAG, "disconnectPlayerServer: shutting down grpc client");
        try {
            channel.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.w(TAG, "Caught exception on disconnect: " + Objects.requireNonNull(e.getMessage()));
        } finally {
            channel = null;
            musicPlayerStub = null;
            dataManagerStub = null;
        }

        mLocalBroadcastManager.sendBroadcast(Message.DISCONNECTED.toIntent());
        Log.i(TAG, "disconnectPlayerServer: grpc client terminated");
    }
    //endregion

    //region LocalBroadcaster
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        assert receiver != null && filter != null;
        Log.d(TAG, "Registering receiver...");
        mLocalBroadcastManager.registerReceiver(receiver, filter);
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        assert receiver != null;
        Log.d(TAG, "Unregistering receiver...");
        mLocalBroadcastManager.unregisterReceiver(receiver);
    }
    //endregion

    //region Commands
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

    public void retrieveNewStatus() {
        musicPlayerStub.retrieveMMPStatus(MMPStatusRequest.getDefaultInstance(), statusStreamObserver);
    }

    public void playSong(int songId) {
        Log.i(TAG, "Sent play command for songID: " + songId);
        musicPlayerStub.play(MediaControl.newBuilder()
                .setState(MediaControl.State.PLAY)
                .setSongId(songId)
                .build(), defaultMMPResponseStreamObserver);
    }

    public void playPause() {
        Log.i(TAG, "play/pause song");
        musicPlayerStub.play(MediaControl.newBuilder()
                .setState(MediaControl.State.PAUSE)
                .build(), defaultMMPResponseStreamObserver);
    }

    public void previous() {
        Log.i(TAG, "Go to previous song");
        musicPlayerStub.previous(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
    }

    public void next() {
        Log.i(TAG, "Go to next song");
        musicPlayerStub.next(PlaybackControl.getDefaultInstance(), defaultMMPResponseStreamObserver);
    }

    public void changeVolume(int newVolume) {
        checkChannelConnected();
        Log.i(TAG, "Changing volume of song to " + newVolume);
        musicPlayerStub.changeVolume(VolumeControl.newBuilder().setVolumeLevel(newVolume).build(), defaultMMPResponseStreamObserver);
    }

    public void changePosition(int position) {
        checkChannelConnected();
        Log.i(TAG, "Changing position of song to " + position);
        musicPlayerStub.changePosition(PositionControl.newBuilder()
                .setPosition(position).build(), defaultMMPResponseStreamObserver);
    }

    public void addSongNext(int songId) {
        checkChannelConnected();
        Log.i(TAG, "Sent addSongNext command for songID: " + songId);
        musicPlayerStub.addNext(MediaData.newBuilder()
                .setType(MediaType.SONG)
                .setId(songId).build(), defaultMMPResponseStreamObserver);
    }

    public void renameSong(int songId, String newTitle) {
        checkChannelConnected();
        Log.i(TAG, "Rename song id=" + songId + " to '" + newTitle + "'");
        dataManagerStub.renameSong(RenameData.newBuilder()
                .setId(songId)
                .setNewTitle(newTitle).build(), defaultMMPResponseStreamObserver);
    }

    public void deleteSong(int songId) {
        checkChannelConnected();
        Log.i(TAG, "Deleting song id=" + songId);
        dataManagerStub.deleteSong(MediaData.newBuilder()
                .setId(songId).build(), defaultMMPResponseStreamObserver);
    }

    public void moveSong(int songId, int albumId) {
        checkChannelConnected();
        Log.i(TAG, "Move song id=" + songId + " to albumid=" + albumId);
        dataManagerStub.moveSong(MoveData.newBuilder()
                .setSongId(songId)
                .setAlbumId(albumId)
                .build(), defaultMMPResponseStreamObserver);
    }

    final StreamObserver<MMPStatus> statusStreamObserver = new StreamObserver<MMPStatus>() {
        @Override
        public void onNext(MMPStatus newState) {
            Log.d(TAG, String.format("onNext in streamObserver with state %s", newState));
            appPlayer.setState(newState);
            assert playerNotificationManager != null && appPlayer != null;
            if (newState.getState() == MMPStatus.State.PLAYING) {
                startForeground(PlayerNotificationManager.NOTIFICATION_ID, playerNotificationManager.createNotification(appPlayer));
            } else {
//                stopForeground(false);
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

    public StreamObserver<MMPResponse> defaultMMPResponseStreamObserver = new StreamObserver<MMPResponse>() {
        @Override
        public void onNext(MMPResponse response) {
            if (response.getResult() == MMPResponse.Result.ERROR
                    && !response.getError().isEmpty()) {
                Log.w(TAG, "GRPC SERVER: " + response.getError());
            }
            if (!response.getMessage().isEmpty()) {
                Log.i(TAG, "GRPC SERVER: " + response.getMessage());
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() { }
    };
    //endregion
}
