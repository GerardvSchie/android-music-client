package nl.melledijkstra.musicplayerclient.data.broadcaster.player.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Album implements Protoble<nl.melledijkstra.musicplayerclient.grpc.Album> {
    public long ID;
    public String Title;
    public boolean Favorite;
    public Bitmap Cover;
    public ArrayList<Song> SongList;

    public Album(nl.melledijkstra.musicplayerclient.grpc.Album exchangeData) {
        SongList = new ArrayList<>();
        Hydrate(exchangeData);
    }

    public Album(long id, String title, @Nullable Bitmap cover, boolean favorite) {
        this(id, title, favorite);
        Cover = cover;
    }

    public Album(long id, String title, boolean favorite) {
        ID = id;
        Title = title;
        Favorite = favorite;
        Cover = null;
        SongList = new ArrayList<>();
    }

    /**
     * Just like fillSongList but for proto objects
     * @param songList The new song list
     */
    private void fillSongListFromProto(List<nl.melledijkstra.musicplayerclient.grpc.Song> songList) {
        SongList.clear();
        for (nl.melledijkstra.musicplayerclient.grpc.Song song : songList) {
            SongList.add(new Song(song));
        }
    }

    /**
     * Fills the songlist of this album by given songs
     * @param songList The new song list
     */
    public void fillSongList(List<Song> songList) {
        SongList.clear();
        SongList.addAll(songList);
    }

    @NonNull
    @Override
    public String toString() {
        assert Title != null : "Title should not be null";
        return Title;
    }

    @Override
    public void Hydrate(nl.melledijkstra.musicplayerclient.grpc.Album obj) {
        ID = obj.getId();
        Title = obj.getTitle();
        // TODO: implement cover in proto file
        // TODO: implement favorite in proto file
        Favorite = false;
        fillSongListFromProto(obj.getSongListList());
    }
}
