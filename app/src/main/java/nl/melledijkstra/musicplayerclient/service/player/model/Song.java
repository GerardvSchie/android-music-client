package nl.melledijkstra.musicplayerclient.service.player.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>SongModel Model class that has all information about a specific song</p>
 */
public class Song implements Protoble<nl.melledijkstra.musicplayerclient.grpc.Song> {
    public long ID;
    public String Title;
    public long Duration;

    public Song(nl.melledijkstra.musicplayerclient.grpc.Song exchangeData) {
        this.Hydrate(exchangeData);
    }

    public Song(long id, String title, long duration) {
        ID = id;
        Title = title;
        Duration = duration;
    }

    @Override
    public void Hydrate(nl.melledijkstra.musicplayerclient.grpc.Song data) {
        ID = data.getId();
        Title = data.getTitle();
        Duration = data.getDuration();
    }

    @NonNull
    @Override
    public String toString() {
        return Title;
    }

    public static ArrayList<Song> debugList() {
        return new ArrayList<>(Arrays.asList(
            new Song(0, "Artist - Test Song #1", 1000),
            new Song(1, "Artist - Test Song #2", 1000),
            new Song(2, "Artist - Test Song #3", 1000),
            new Song(3, "Artist - Test Song #4", 1000),
            new Song(4, "Artist - Test Song #5", 1000)));
    }
}
