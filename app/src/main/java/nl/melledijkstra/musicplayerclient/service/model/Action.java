package nl.melledijkstra.musicplayerclient.service.model;

import android.content.Intent;
import android.util.Log;

public enum Action {
    ACTION_PLAY_PAUSE("nl.melledijkstra.musicplayerclient.ACTION_PLAY_PAUSE"),
    ACTION_PREV("nl.melledijkstra.musicplayerclient.ACTION_PREV"),
    ACTION_NEXT("nl.melledijkstra.musicplayerclient.ACTION_NEXT"),
    ACTION_REPEAT("nl.melledijkstra.musicplayerclient.ACTION_REPEAT"),
    ACTION_SHUFFLE("nl.melledijkstra.musicplayerclient.ACTION_SHUFFLE"),
    ACTION_CLOSE("nl.melledijksta.melonmusicplayer.ACTION_CLOSE");

    static final String TAG = "Action";
    final String stringValue;

    Action(String value) {
        stringValue = value;
    }

    public static Action getAction(Intent intent) {
        String actionString = intent.getAction();
        try {
            Action action = Action.valueOf(actionString);
            Log.d(TAG, "Action '" + action + "' parsed");
            return action;
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Action '" + actionString + "' could not be parsed, returning null");
            return null;
        }
    }
}
