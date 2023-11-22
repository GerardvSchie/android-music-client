package nl.melledijkstra.musicplayerclient.ui.fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.grpc.stub.StreamObserver;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.grpc.MediaControl;
import nl.melledijkstra.musicplayerclient.grpc.MediaData;
import nl.melledijkstra.musicplayerclient.grpc.MediaType;
import nl.melledijkstra.musicplayerclient.grpc.MoveData;
import nl.melledijkstra.musicplayerclient.grpc.RenameData;
import nl.melledijkstra.musicplayerclient.grpc.Song;
import nl.melledijkstra.musicplayerclient.grpc.SongList;
import nl.melledijkstra.musicplayerclient.melonplayer.AlbumModel;
import nl.melledijkstra.musicplayerclient.melonplayer.SongModel;
import nl.melledijkstra.musicplayerclient.ui.adapters.SongAdapter;

public class SongsFragment extends ServiceBoundFragment implements SongAdapter.RecyclerItemClickListener, OnMenuItemClickListener {
    static final String TAG = "SongsFragment";
    SwipeRefreshLayout refreshSwipeLayout;
    RecyclerView songListRecyclerView;
    Unbinder unbinder;
    long albumid;
    ArrayList<SongModel> songModels;
    // ListAdapter that dynamically fills the music list
    SongAdapter songListAdapter;
    ContentLoadingProgressBar progressBarSong;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songModels = new ArrayList<>();
        Log.d(TAG, "Fragment Created");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_songs, container, false);
        unbinder = ButterKnife.bind(this, layout);

        // refreshlayout
        refreshSwipeLayout = layout.requireViewById(R.id.song_swipe_refresh_layout);
        refreshSwipeLayout.setOnRefreshListener(onRefreshListener);

        // recyclerview
        songListAdapter = new SongAdapter(songModels, this, this);
        songListRecyclerView = layout.requireViewById(R.id.songListView);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songListRecyclerView.setAdapter(songListAdapter);
        songListRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        // dialog
        progressBarSong = layout.requireViewById(R.id.progressBarSong);
        progressBarSong.setIndeterminate(true);
        progressBarSong.setProgressTintList(ColorStateList.valueOf(Color.RED));

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Integer position = songListAdapter.getPosition();
        assert position != null : "Song list adapter";
        final SongModel songModel = songModels.get(position);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_play_next) {
            if (isBound) {
                boundService.musicPlayerStub.addNext(MediaData.newBuilder()
                        .setType(MediaType.SONG)
                        .setId(songModel.getID()).build(), boundService.defaultMMPResponseStreamObserver);
            }
        } else if (itemId == R.id.menu_rename) {
            View renameSongDialog = requireActivity().getLayoutInflater().inflate(R.layout.rename_song_dialog, null);
            final EditText edRenameSong = renameSongDialog.requireViewById(R.id.edRenameSong);
            edRenameSong.setText(songModel.getTitle());
            new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_mode_edit)
                    .setTitle(R.string.rename)
                    .setView(renameSongDialog)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.rename, (dialog, which) -> {
                        if (isBound) {
                            boundService.dataManagerStub.renameSong(RenameData.newBuilder()
                                    .setId(songModel.getID())
                                    .setNewTitle(edRenameSong.getText().toString()).build(), boundService.defaultMMPResponseStreamObserver);
                        }
                    }).show();
        } else if (itemId == R.id.menu_move) {
            if (isBound) {
                View moveSongDialog = requireActivity().getLayoutInflater().inflate(R.layout.move_song_dialog, null);
                final Spinner spinnerAlbums = moveSongDialog.requireViewById(R.id.spinnerAlbums);
                spinnerAlbums.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, boundService.getMelonPlayer().albumModels));
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_reply)
                        .setTitle(R.string.move)
                        .setView(moveSongDialog)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.move, (dialog, which) -> {
                            AlbumModel selectedAlbum = ((AlbumModel) spinnerAlbums.getSelectedItem());
                            if (selectedAlbum != null) {
                                boundService.dataManagerStub.moveSong(MoveData.newBuilder()
                                        .setSongId(songModel.getID())
                                        .setAlbumId(selectedAlbum.getID())
                                        .build(), boundService.defaultMMPResponseStreamObserver);
                            }
                        }).show();
            }
        } else if (itemId == R.id.menu_delete) {
            new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.ic_action_trash)
                    .setTitle(R.string.delete)
                    .setMessage("Do you really want to delete '" + songModel.getTitle() + "'?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        if (isBound) {
                            boundService.dataManagerStub.deleteSong(MediaData.newBuilder()
                                    .setId(songModel.getID()).build(), boundService.defaultMMPResponseStreamObserver);
                        }
                    }).show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onBounded() {
        retrieveSongList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "albumid: " + albumid);
        albumid = requireArguments().getLong("albumid", -1);
        retrieveSongList();
    }

    private void retrieveSongList() {
        if (isBound) {
            progressBarSong.setVisibility(View.VISIBLE);
            // TODO: move this to service
            boundService.musicPlayerStub.retrieveSongList(MediaData.newBuilder().setId(albumid).build(), new StreamObserver<SongList>() {
                @Override
                public void onNext(final SongList response) {
                    songModels.clear();
                    for (Song song : response.getSongListList()) {
                        songModels.add(new SongModel(song));
                    }
                    requireActivity().runOnUiThread(() -> songListAdapter.notifyDataSetChanged());
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "grpc onError: ", t);
                }

                @Override
                public void onCompleted() {
                    Log.i(TAG, "onCompleted: retrieving songs done");
                    requireActivity().runOnUiThread(() -> {
                        progressBarSong.setVisibility(View.GONE);
                        refreshSwipeLayout.setRefreshing(false);
                    });
                }
            });

            return;
        }

        if (App.DEBUG) {
            songModels.clear();
            Collections.addAll(songModels,
                    new SongModel(0, "Artist - Test Song #1", 1000),
                    new SongModel(1, "Artist - Test Song #1", 1000),
                    new SongModel(2, "Artist - Test Song #1", 1000),
                    new SongModel(3, "Artist - Test Song #1", 1000),
                    new SongModel(4, "Artist - Test Song #1", 1000));
            songListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * SongModel ListView Refresh action that populates the ListView with songs
     */
    final SwipeRefreshLayout.OnRefreshListener onRefreshListener = this::retrieveSongList;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressBarSong.isShown()) {
            progressBarSong.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (isBound) {
            boundService.musicPlayerStub.play(MediaControl.newBuilder()
                    .setState(MediaControl.State.PLAY)
                    .setSongId(songModels.get(position).getID())
                    .build(), boundService.defaultMMPResponseStreamObserver);
        }
    }
}
