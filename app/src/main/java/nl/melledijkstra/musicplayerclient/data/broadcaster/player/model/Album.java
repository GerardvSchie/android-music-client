package nl.melledijkstra.musicplayerclient.data.broadcaster.player.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Album implements Protoble<nl.melledijkstra.musicplayerclient.grpc.Album> {
    public long ID;
    public String Title;
    public boolean Favorite;
    public Bitmap Cover;
    public ArrayList<Song> SongList = new ArrayList<>();

    public Album(nl.melledijkstra.musicplayerclient.grpc.Album exchangeData) {
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
    }

    public int getSongCount() {
        return SongList.size();
    }

    public Song getSong(int i) {
        return i >= getSongCount() ? null : SongList.get(i);
    }

    private void fillSongListFromProto(List<nl.melledijkstra.musicplayerclient.grpc.Song> songList) {
        SongList.clear();
        for (nl.melledijkstra.musicplayerclient.grpc.Song song : songList) {
            SongList.add(new Song(song));
        }
    }

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

    public static ArrayList<Album> debugList() {
        return new ArrayList<>(Arrays.asList(
            new Album(1, "Chill", true),
            new Album(2, "House", false),
            new Album(3, "Classic", true),
            new Album(4, "Future House", false),
            new Album(5, "Test", false),
            new Album(6, "Another AlbumModel", false)));
    }

    public static Album debug() {
        Album album = new Album(0, "DebugAlbum", false);
        album.SongList = Song.debugList();
        return album;
    }
}
