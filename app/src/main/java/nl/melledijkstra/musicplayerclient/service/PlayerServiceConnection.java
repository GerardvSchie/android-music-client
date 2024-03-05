package nl.melledijkstra.musicplayerclient.service;

public interface PlayerServiceConnection extends PlayerService {
    void start(String IP, int port);
    void stop();
}
