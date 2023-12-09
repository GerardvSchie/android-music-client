package nl.melledijkstra.musicplayerclient.ui.main.controller;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.ui.base.MPCView;
import nl.melledijkstra.musicplayerclient.utils.PlayerTimer;

public interface ControllerMPCView extends MPCView {
    void updatePlayPause(AppPlayer appPlayer);
    void updateProgressBar(AppPlayer appPlayer, PlayerTimer playerTimer);
}
