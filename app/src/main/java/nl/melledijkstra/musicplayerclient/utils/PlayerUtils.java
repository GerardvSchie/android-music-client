package nl.melledijkstra.musicplayerclient.utils;

import androidx.annotation.Nullable;

import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;

public class PlayerUtils {
    private PlayerUtils() {
        // This utility class is not publicly instantiable
    }

    // Finds an Album by ID
    @Nullable
    public static Album findAlbumByID(Iterable<Album> albums, long albumId) {
        for (Album album : albums) {
            if (album.ID == albumId) {
                return album;
            }
        }
        return null;
    }

    // Find song by given ID
    @Nullable
    public static Song findSongByID(Iterable<Album> albums, long songId) {
        for (Album album : albums) {
            for (Song song : album.SongList) {
                if (song.ID == songId) {
                    return song;
                }
            }
        }
        return null;
    }
}
