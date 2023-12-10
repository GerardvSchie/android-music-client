package nl.melledijkstra.musicplayerclient.ui.main;

import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.MPCView;

public interface MainMPCView extends MPCView {
    void openConnectActivity();
    void openSettingsActivity();
    void showAlbumFragment();
    void showSongFragment(Album album);
    void onConnect();
}
