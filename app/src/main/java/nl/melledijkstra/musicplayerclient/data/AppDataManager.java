package nl.melledijkstra.musicplayerclient.data;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.melledijkstra.musicplayerclient.data.prefs.PreferencesHelper;
import nl.melledijkstra.musicplayerclient.di.ApplicationContext;

@Singleton
public class AppDataManager implements DataManager {
    public final Context mContext;
    public final PreferencesHelper mPreferencesHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context, PreferencesHelper preferencesHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public String getCurrentHostIP() {
        return mPreferencesHelper.getCurrentHostIP();
    }

    @Override
    public void setCurrentHostIP(String ip) {
        mPreferencesHelper.setCurrentHostIP(ip);
    }

    @Override
    public int getCurrentHostPort() {
        return mPreferencesHelper.getCurrentHostPort();
    }

    @Override
    public void setCurrentHostPort(int port) {
        mPreferencesHelper.setCurrentHostPort(port);
    }

    @Override
    public boolean getDebug() {
        return mPreferencesHelper.getDebug();
    }

    @Override
    public void setDebug(boolean debug) {
        mPreferencesHelper.setDebug(debug);
    }
}
