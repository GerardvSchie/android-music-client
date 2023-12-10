package nl.melledijkstra.musicplayerclient.ui.main.controller;

import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.ui.base.MPCPresenter;

public interface ControllerMPCPresenter<V extends ControllerMPCView> extends MPCPresenter<V> {
    void startTimer();
    void stopTimer();
    void resetTimer();
    void playPause();
    void previous();
    void next();
    void changePosition(int position);
    void changeVolume(int volume);
    AppPlayer appPlayer();
}
