package nl.melledijkstra.musicplayerclient.service.player;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

import nl.melledijkstra.musicplayerclient.service.player.model.PlayerState;
import nl.melledijkstra.musicplayerclient.service.player.model.Song;
import nl.melledijkstra.musicplayerclient.grpc.MMPStatus;

/**
 * This class represents the Model object for the music player.
 * It's stores and updates the state of the music player server
 */
public class AppPlayer {
    static final String TAG = "AppPlayer";

    public PlayerState State = PlayerState.NOTHING_SPECIAL;
    public int Volume = -1;
    public boolean Mute = false;
    public Song CurrentSong = null;
    public float SongPosition = -1f;
    public long SongElapsedTime = -1;

    final StateUpdateListener stateListener;

    public AppPlayer(StateUpdateListener stateUpdateListener) {
        stateListener = stateUpdateListener;
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
        stateListener.PlayerStateUpdated();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[state: %s, volume: %d, muted: %b, song: %s, position: %f, ellapsedTime: %d]", State, Volume, Mute, CurrentSong, SongPosition, SongElapsedTime);
    }

    public interface StateUpdateListener {
        // Invoked when status is changed
        void PlayerStateUpdated();
    }
}
