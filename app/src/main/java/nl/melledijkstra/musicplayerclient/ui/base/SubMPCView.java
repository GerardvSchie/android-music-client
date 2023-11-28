package nl.melledijkstra.musicplayerclient.ui.base;

public interface SubMPCView extends MPCView {
    void onCreate();
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
    void attachParentMvpView(MPCView mvpView);
}
