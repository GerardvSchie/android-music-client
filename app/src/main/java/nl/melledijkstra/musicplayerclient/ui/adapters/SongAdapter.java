package nl.melledijkstra.musicplayerclient.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.Utils;
import nl.melledijkstra.musicplayerclient.melonplayer.SongModel;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    static final String TAG = "SongAdapter";

    /**
     * Click itemClickListener for items in recyclerview
     */
    final RecyclerItemClickListener itemClickListener;
    private final OnMenuItemClickListener menuListener;

    final ArrayList<SongModel> songModels;
    private Integer currentPopupPosition;

    public SongAdapter(ArrayList<SongModel> songModels, RecyclerItemClickListener itemClickListener,
                       OnMenuItemClickListener onMenuItemClickListener) {
        this.songModels = songModels;
        this.itemClickListener = itemClickListener;
        this.menuListener = onMenuItemClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        SongModel songModel = (position <= songModels.size()) ? songModels.get(position) : null;

        final int hPosition = holder.getBindingAdapterPosition();
        holder.itemView.setOnClickListener(view -> itemClickListener.onItemClick(view, hPosition));

        holder.tvSongOptions.setOnClickListener(view -> {
            currentPopupPosition = hPosition;
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.song_item_menu);
            popup.setOnMenuItemClickListener(menuListener);
            popup.show();
        });

        holder.tvTitle.setText(songModel != null ? songModel.getTitle() : null);
        if (songModel != null && songModel.getDuration() != 0) {
            holder.tvDuration.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            holder.tvDuration.setText(Utils.secondsToDurationFormat(songModel.getDuration()));
        } else {
            holder.tvDuration.setTypeface(null, Typeface.ITALIC);
            holder.tvDuration.setText(R.string.undefined);
        }
    }

    /**
     * Gets the position of item where popup is currently shown, too bad Android doesn't make this a little easier
     * @return The position of item where popup is shown
     */
    public Integer getPosition() {
        return currentPopupPosition;
    }

    @Override
    public int getItemCount() {
        return songModels.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle = itemView.requireViewById(R.id.song_title);
        TextView tvDuration = itemView.requireViewById(R.id.song_duration);
        TextView tvSongOptions = itemView.requireViewById(R.id.song_option_btn);

        SongViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface RecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
}
