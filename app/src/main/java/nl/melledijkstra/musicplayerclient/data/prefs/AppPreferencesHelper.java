package nl.melledijkstra.musicplayerclient.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.melledijkstra.musicplayerclient.di.ApplicationContext;
import nl.melledijkstra.musicplayerclient.utils.AppConstants;

/**
 * <p>This class specifies all preference keys that can be used
 * in the application to store and get from the right place</p>
 */
@Singleton
public class AppPreferencesHelper implements PreferencesHelper {
    // The remote ip connection to be used for socket connection
    public static final String PREF_KEY_HOST_IP = "host_ip";
    // The port of the server
    public static final String PREF_KEY_HOST_PORT = "host_port";
    // Debug state. If true then remote connection is simulated.
    public static final String PREF_KEY_DEBUG = "debug";

    final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getCurrentHostIP() {
        return mPrefs.getString(PREF_KEY_HOST_IP, AppConstants.DEFAULT_HOST_IP);
    }

    public void setCurrentHostIP(String ip) {
        assert ip != null : "IP cannot be null";
        mPrefs.edit().putString(PREF_KEY_HOST_IP, ip).apply();
    }

    public int getCurrentHostPort() {
        return mPrefs.getInt(PREF_KEY_HOST_PORT, AppConstants.DEFAULT_HOST_PORT);
    }

    public void setCurrentHostPort(int port) {
        mPrefs.edit().putInt(PREF_KEY_HOST_PORT, port).apply();
    }

    public boolean getDebug() {
        return mPrefs.getBoolean(PREF_KEY_DEBUG, false);
    }

    public void setDebug(boolean debug) {
        mPrefs.edit().putBoolean(PREF_KEY_DEBUG, debug).apply();
    }
}
