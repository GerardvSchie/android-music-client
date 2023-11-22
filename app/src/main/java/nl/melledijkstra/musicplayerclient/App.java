package nl.melledijkstra.musicplayerclient;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import nl.melledijkstra.musicplayerclient.config.PreferenceKeys;

public class App extends Application {
    static final String TAG = "musicplayerclient";

    // If app is in DEBUG mode then no connection is needed and dummy data is used
    public static boolean DEBUG = false;

    public App() {}

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DEBUG = prefs.getBoolean(PreferenceKeys.DEBUG, false);
    }

    // Checks if the debug state has changed and sets the debug state for the application
    public void checkDebugState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DEBUG = prefs.getBoolean(PreferenceKeys.DEBUG, false);
    }
}
