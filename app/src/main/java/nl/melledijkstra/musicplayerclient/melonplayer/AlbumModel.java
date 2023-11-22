package nl.melledijkstra.musicplayerclient.melonplayer;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import nl.melledijkstra.musicplayerclient.grpc.Album;
import nl.melledijkstra.musicplayerclient.grpc.Song;

public class AlbumModel implements Protoble<Album> {
    long ID;
    String title;
    boolean favorite;
    @Nullable
    Bitmap cover;
    ArrayList<SongModel> songModelList;

    public AlbumModel(Album exchangeData) {
        this.songModelList = new ArrayList<>();
        this.Hydrate(exchangeData);
    }

    public AlbumModel(long id, String title, @Nullable Bitmap cover, boolean favorite) {
        this(id, title, favorite);
        this.cover = cover;
    }

    public AlbumModel(long id, String title, boolean favorite) {
        this.ID = id;
        this.title = title;
        this.favorite = favorite;
        this.cover = null;
        this.songModelList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public Bitmap getCover() {
        return cover;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public long getID() {
        return ID;
    }

    public ArrayList<SongModel> getSongList() {
        return songModelList;
    }

    /**
     * Just like fillSongList but for proto objects
     * @param songlist The new song list
     */
    private void fillSongListFromProto(List<Song> songlist) {
        songModelList.clear();
        for (Song song : songlist) {
            songModelList.add(new SongModel(song));
        }
    }

    /**
     * Fills the songlist of this album by given songs
     * @param songlist The new song list
     */
    public void fillSongList(List<SongModel> songlist) {
        songModelList.clear();
        songModelList.addAll(songlist);
    }

    @NonNull
    @Override
    public String toString() {
        assert title != null : "Title should not be null";
        return title;
    }

    @Override
    public void Hydrate(Album obj) {
        ID = obj.getId();
        title = obj.getTitle();
        // TODO: implement cover in proto file
        // TODO: implement favorite in proto file
        favorite = false;
        fillSongListFromProto(obj.getSongListList());
    }
}
