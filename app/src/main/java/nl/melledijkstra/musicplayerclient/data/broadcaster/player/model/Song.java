package nl.melledijkstra.musicplayerclient.data.broadcaster.player.model;

import androidx.annotation.NonNull;

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
}
