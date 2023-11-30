package nl.melledijkstra.musicplayerclient.data.broadcaster.player.model;

import android.util.Log;

import nl.melledijkstra.musicplayerclient.grpc.MMPStatus;

// The different states the melon player can reside in
public enum PlayerState {
    BUFFERING,
    PLAYING,
    ENDED,
    ERROR,
    NOTHING_SPECIAL,
    OPENING,
    PAUSED,
    STOPPED;

    static final String TAG = "PlayerState";

    public static PlayerState valueOfOrNothing(MMPStatus.State state) {
        try {
            PlayerState playerState = PlayerState.valueOf(state.toString());
            Log.d(TAG, "Parsed: " + playerState);
            return playerState;
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "'" + state + "' could not be parsed to a PlayerState, returning NOTHING_SPECIAL");
            return NOTHING_SPECIAL;
        }
    }
}
