package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.ui.base.BaseViewHolder;
import nl.melledijkstra.musicplayerclient.utils.MathUtils;

public class SongAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    static final String TAG = "SongAdapter";
    private Album album = null;
    private int custom_layout_id;

    public void setAlbum(Album album) {
        this.album = album;
    }
    public Album album() {
        return album;
    }

    public SongAdapter() {

    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public Integer getPosition() {
        return custom_layout_id;
    }

    @Override
    public int getItemCount() {
        if (album == null) {
            return 0;
        }
        return album.SongList.size();
    }

    public class SongViewHolder extends BaseViewHolder {
        static final String TAG = "SongViewHolder";
        TextView tvTitle = itemView.requireViewById(R.id.song_title);
        TextView tvDuration = itemView.requireViewById(R.id.song_duration);
        TextView tvSongOptions = itemView.requireViewById(R.id.song_option_btn);

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            tvTitle.setText("");
            tvDuration.setText("");
            tvSongOptions.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            if (position >= getItemCount()) {
                Log.w(TAG, "onBind called with illegal position");
                return;
            }

            Song song = album.SongList.get(position);

            tvTitle.setText(song.Title);
            if (song.Duration == 0) {
                tvDuration.setTypeface(null, Typeface.ITALIC);
                tvDuration.setText(R.string.undefined);
            } else {
                tvDuration.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tvDuration.setText(MathUtils.secondsToDurationFormat(song.Duration));
            }

//            final int hPosition = getBindingAdapterPosition();
//            holder.itemView.setOnClickListener(view -> itemClickListener.onItemClick(view, hPosition));

            tvSongOptions.setOnClickListener(view -> {
//                hPosition = custom_layout_id;
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.song_item_menu);
//                popup.setOnMenuItemClickListener(menuListener);
                popup.show();
            });
        }
    }
}
