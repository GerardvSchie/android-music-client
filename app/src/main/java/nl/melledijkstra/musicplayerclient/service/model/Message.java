package nl.melledijkstra.musicplayerclient.service.model;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

// BROADCAST MESSAGES
public enum Message {
    // INCOMING
    INITIATE_CONNECTION("nl.melledijkstra.musicplayerclient.INITIATE_CONNECTION"),

    // OUTGOING
    READY("nl.melledijkstra.musicplayerclient.READY"),
    DISCONNECTED("nl.melledijkstra.musicplayerclient.DISCONNECTED"),
    CONNECT_FAILED("nl.melledijkstra.musicplayerclient.CONNECT_FAILED");

    static final String TAG = "Message";
    final String stringValue;

    Message(String value) {
        stringValue = value;
    }

    public static Message getMessage(Intent intent) {
        String message = intent.getAction();
        try {
            return Message.valueOf(message);
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Message: '" + message + "' could not be parsed, returning null");
            return null;
        }
    }

    public Intent toIntent() {
        return new Intent(toString());
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
