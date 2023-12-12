package nl.melledijkstra.musicplayerclient.service;

import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;

public interface PlayerService extends AppPlayer.StateUpdateListener {
    boolean isConnected();
    void connectPlayerServer(String ip, int port);
    void disconnectPlayerServer();
    void retrieveAlbumList(AlbumMPCView mView);
    void retrieveSongList(int albumID, SongMPCView mView);
    AppPlayer getAppPlayer();
    void retrieveNewStatus();
    void playSong(int songId);
    void playPause();
    void previous();
    void next();
    void changeVolume(int newVolume);
    void changePosition(int position);
    void addSongNext(int songId);
    void renameSong(int songId, String newTitle);
    void deleteSong(int songId);
    void moveSong(int songId, int albumId);
}
