package nl.melledijkstra.musicplayerclient.ui.main;

import nl.melledijkstra.musicplayerclient.ui.base.MPCView;

public interface MainMPCView extends MPCView {
    void openConnectActivity();
    void openSettingsActivity();
    void next();
}
