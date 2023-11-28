package nl.melledijkstra.musicplayerclient.data.broadcaster.player;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.PlayerState;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatus;

/**
 * This class represents the Model object for the music player.
 * It's stores and updates the state of the music player server
 */
public class AppPlayer implements Player {
    static final String TAG = "AppPlayer";

    public PlayerState State = PlayerState.NOTHING_SPECIAL;
    public ArrayList<Album> Albums = new ArrayList<>();
    public int Volume = -1;
    public boolean Mute = false;
    public Song CurrentSong = null;
    public float SongPosition = -1f;
    public long SongElapsedTime = -1;

    final HashSet<StateUpdateListener> stateListeners = new HashSet<>();
    // The singleton instance
    static AppPlayer instance;

    public void registerStateChangeListener(StateUpdateListener listener) {
        stateListeners.add(listener);
    }

    public void unRegisterStateChangeListener(StateUpdateListener listener) {
        stateListeners.remove(listener);
    }

    private AppPlayer() {
        // Private constructor so only static instance can be made
    }

    /**
     * Get the one and only instance of MelonPlayer object
     * see Singleton Pattern
     * @return The one and only instance of this class
     */
    public static AppPlayer getInstance() {
        if (instance == null) {
            instance = new AppPlayer();
        }
        return instance;
    }

    // Set the new state of the melon player
    public void setState(MMPStatus status) {
        Log.i(TAG, "Setting new state of MelonPlayer");
        CurrentSong = new Song(status.getCurrentSong());
        State = PlayerState.valueOfOrNothing(status.getState());
        Volume = status.getVolume();
        SongPosition = status.getPosition();
        SongElapsedTime = status.getElapsedTime();
        Mute = status.getMute();

        Log.d(TAG, String.format(Locale.getDefault(), "%s '%s' volume: %d, position: %f, mute: %b", State, CurrentSong, Volume, SongPosition, Mute));
        for (StateUpdateListener listener : stateListeners) {
            listener.MelonPlayerStateUpdated();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[state: %s, volume: %d, album count: %d]", State, Volume, Albums.size());
    }

    @Override
    public PlayerState getPlayerState() {
        return State;
    }

    public interface StateUpdateListener {
        // Invoked when status is changed
        void MelonPlayerStateUpdated();
    }
}
