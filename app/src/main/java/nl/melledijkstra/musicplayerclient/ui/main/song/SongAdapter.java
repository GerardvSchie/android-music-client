package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import nl.melledijkstra.musicplayerclient.utils.AlertUtils;
import nl.melledijkstra.musicplayerclient.utils.MathUtils;

public class SongAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    static final String TAG = "SongAdapter";
    private Callback mCallback;
    private Album album = null;
    public void setAlbum(Album album) {
        this.album = album;
    }
    public Album album() {
        return album;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
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

    @Override
    public int getItemCount() {
        return album == null ? 0 : album.getSongCount();
    }

    public interface Callback {
        void play(int songId);
        void addNext(int songId);
        void renameSong(int songId, String newTitle);
        void deleteSong(int songId);
        void moveSong(int songId, int albumId);
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

            tvSongOptions.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.song_item_menu);
                popup.setOnMenuItemClickListener(item -> SongViewHolder.this.onMenuItemClick(item, song));
                popup.show();
            });

            itemView.setOnClickListener(view -> mCallback.play((int)song.ID));
        }

        public boolean onMenuItemClick(MenuItem item, Song song) {
            Log.d(TAG, "Clicked on menu item: " + item.getTitle());
            int itemId = item.getItemId();
            if (itemId == R.id.menu_play_next) {
                mCallback.addNext((int)song.ID);
            } else if (itemId == R.id.menu_rename) {
//                View renameSongDialog = requireActivity().getLayoutInflater().inflate(R.layout.rename_song_dialog, null);
//                final EditText edRenameSong = renameSongDialog.requireViewById(R.id.edRenameSong);
//                edRenameSong.setText(song.Title);
//                AlertUtils.createAlert(getContext(), R.drawable.ic_mode_edit, String.valueOf(R.string.rename), renameSongDialog)
//                        .setNegativeButton(R.string.cancel, null)
//                        .setPositiveButton(R.string.rename, (dialog, which) -> {
//                            if (isBound) {
//                                boundService.dataManagerStub.renameSong(RenameData.newBuilder()
//                                        .setId(song.ID)
//                                        .setNewTitle(edRenameSong.getText().toString()).build(), boundService.defaultMMPResponseStreamObserver);
//                            }
//                        }).show();
            } else if (itemId == R.id.menu_move) {
//                View moveSongDialog = requireActivity().getLayoutInflater().inflate(R.layout.move_song_dialog, null);
//                final Spinner spinnerAlbums = moveSongDialog.requireViewById(R.id.spinnerAlbums);
//                spinnerAlbums.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, boundService.getMelonPlayer().Albums));
//                AlertUtils.createAlert(getContext(), R.drawable.ic_reply, "Move", moveSongDialog)
//                        .setNegativeButton(R.string.cancel, null)
//                        .setPositiveButton(R.string.move, (dialog, which) -> {
//                            Album selectedAlbum = ((Album) spinnerAlbums.getSelectedItem());
//                            if (selectedAlbum != null) {
//                                boundService.dataManagerStub.moveSong(MoveData.newBuilder()
//                                        .setSongId(song.ID)
//                                        .setAlbumId(selectedAlbum.ID)
//                                        .build(), boundService.defaultMMPResponseStreamObserver);
//                            }
//                        }).show();
            } else if (itemId == R.id.menu_delete) {
                AlertUtils.createAlert(itemView.getContext(), R.drawable.ic_action_trash, String.valueOf(R.string.delete), null)
                        .setMessage("Do you really want to delete '" + song.Title + "'?")
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, (dialog, which) -> mCallback.deleteSong((int) song.ID)).show();
            }

            return true;
        }
    }
}
