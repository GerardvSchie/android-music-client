package nl.melledijkstra.musicplayerclient.ui.connect;

import nl.melledijkstra.musicplayerclient.ui.base.MPCView;

public interface ConnectMPCView extends MPCView {
    void openMainActivity();
    void openSettingsActivity();
    void failConnection();
}
