package nl.melledijkstra.musicplayerclient.ui.main.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;

public class AlbumAdapter extends BaseAdapter {
    static final String TAG = "AlbumAdapter";

    final Context mContext;
    final ArrayList<Album> albums;

    public AlbumAdapter(Context c, ArrayList<Album> albums) {
        this.mContext = c;
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Album getItem(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: This method needs improvement
        View item;
        Album album = (position <= albums.size()) ? albums.get(position) : null;
        if(convertView == null) {
            item = LayoutInflater.from(mContext).inflate(R.layout.album_item, null);
        } else {
            item = convertView;
        }

        // AlbumModel title
        TextView textView = item.requireViewById(R.id.album_title);
        // AlbumModel cover
        ImageView imageView = item.requireViewById(R.id.album_cover);
        // Favorite btn
//        final ImageView favoriteImage = (ImageView) item.findViewById(R.id.favoriteImageView);
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
        textView.setText(album != null ? album.Title : null);
        Bitmap cover = null;
        if (album != null) {
            cover = album.Cover;
        }
        imageView.setImageBitmap((cover != null) ? cover : ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.default_cover))).getBitmap());

        return item;
    }
}
