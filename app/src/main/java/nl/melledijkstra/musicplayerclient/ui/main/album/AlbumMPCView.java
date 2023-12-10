package nl.melledijkstra.musicplayerclient.ui.main.album;

import java.util.ArrayList;

import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.MPCView;

public interface AlbumMPCView extends MPCView {
    void updateAlbums(ArrayList<Album> albumList);
    void stopRefresh(boolean hasDelay);
}
