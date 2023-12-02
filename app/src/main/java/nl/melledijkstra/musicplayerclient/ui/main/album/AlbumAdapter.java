package nl.melledijkstra.musicplayerclient.ui.main.album;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;

public class AlbumAdapter extends ArrayAdapter<Album> {
    static final String TAG = "AlbumAdapter";
    private ArrayList<Album> albums = new ArrayList<>();

    public AlbumAdapter(Context context, int resource) {
        super(context, resource);
        Log.i(TAG, String.format("Created with %d albums", getCount()));
    }

    @Nullable
    @Override
    public Album getItem(int position) {
        if (position >= getCount()) {
            return null;
        }

        return albums.get(position);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @NonNull
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.v(TAG, "getView of i=" + i);
        Context context = viewGroup.getContext();
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.album_item, null);
        }

        Album album = getItem(i);
        assert album != null;

        TextView textView = view.requireViewById(R.id.album_title);
        textView.setText(album.Title);

        ImageView imageView = view.requireViewById(R.id.album_cover);
        if (album.Cover != null) {
            imageView.setImageBitmap(album.Cover);
        } else {
            imageView.setImageBitmap(((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.default_cover))).getBitmap());
        }

//        // Favorite btn
//        ImageView favoriteImage = view.requireViewById(R.id.favoriteImageView);
//        favoriteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onPreviousClick(View v) {
//                // TODO: Make albumModel actually favorite
//                if(true) { //albumModel != null && albumModel.isFavorite()) {
//                    favoriteImage.setImageResource(R.drawable.ic_action_star_10);
//                } else {
//                    favoriteImage.setImageResource(R.drawable.ic_action_star_0);
//                }
//            }
//        });

        return view;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<Album> albums() {
        return albums;
    }
}
