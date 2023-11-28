package nl.melledijkstra.musicplayerclient.ui.base;

public interface MPCPresenter<V extends MPCView> {
    void onAttach(V view);
    void onDetach();
}
