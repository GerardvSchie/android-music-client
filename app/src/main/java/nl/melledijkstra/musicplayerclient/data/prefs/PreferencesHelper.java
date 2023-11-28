package nl.melledijkstra.musicplayerclient.data.prefs;

public interface PreferencesHelper {
    String getCurrentHostIP();
    void setCurrentHostIP(String ip);
    int getCurrentHostPort();
    void setCurrentHostPort(int port);
    boolean getDebug();
    void setDebug ( boolean debug);
}