package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;

public class SongFragment extends BaseFragment implements SongMPCView, SwipeRefreshLayout.OnRefreshListener, OnMenuItemClickListener {
    static final String TAG = "SongsFragment";

    @Inject
    SongMPCPresenter<SongMPCView> mPresenter;
    @Inject
    SongAdapter mSongAdapter;

    RecyclerView songListRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    int albumId = 0;

    public static SongFragment newInstance() {
        Bundle args = new Bundle();
        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        setUnbinder(ButterKnife.bind(this, view));
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void setUp(View view) {
        requireActivity().setTitle("Songs");

        swipeRefreshLayout = view.requireViewById(R.id.song_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        songListRecyclerView = view.requireViewById(R.id.songListView);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songListRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        songListRecyclerView.setAdapter(mSongAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        Integer position = songListAdapter.getPosition();
//        assert position != null : "Song list adapter";
//        final Song song = songs.get(position);
//        int itemId = item.getItemId();
//        if (itemId == R.id.menu_play_next) {
//            if (isBound) {
//                boundService.musicPlayerStub.addNext(MediaData.newBuilder()
//                        .setType(MediaType.SONG)
//                        .setId(song.ID).build(), boundService.defaultMMPResponseStreamObserver);
//            }
//        } else if (itemId == R.id.menu_rename) {
//            View renameSongDialog = requireActivity().getLayoutInflater().inflate(R.layout.rename_song_dialog, null);
//            final EditText edRenameSong = renameSongDialog.requireViewById(R.id.edRenameSong);
//            edRenameSong.setText(song.Title);
//            AlertUtils.createAlert(getContext(), R.drawable.ic_mode_edit, String.valueOf(R.string.rename), renameSongDialog)
//                    .setNegativeButton(R.string.cancel, null)
//                    .setPositiveButton(R.string.rename, (dialog, which) -> {
//                        if (isBound) {
//                            boundService.dataManagerStub.renameSong(RenameData.newBuilder()
//                                    .setId(song.ID)
//                                    .setNewTitle(edRenameSong.getText().toString()).build(), boundService.defaultMMPResponseStreamObserver);
//                        }
//                    }).show();
//        } else if (itemId == R.id.menu_move) {
//            if (isBound) {
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
//            }
//        } else if (itemId == R.id.menu_delete) {
//            AlertUtils.createAlert(getContext(), R.drawable.ic_action_trash, String.valueOf(R.string.delete), null)
//                    .setMessage("Do you really want to delete '" + song.Title + "'?")
//                    .setNegativeButton(R.string.cancel, null)
//                    .setPositiveButton(R.string.delete, (dialog, which) -> {
//                        if (isBound) {
//                            boundService.dataManagerStub.deleteSong(MediaData.newBuilder()
//                                    .setId(song.ID).build(), boundService.defaultMMPResponseStreamObserver);
//                        }
//                    }).show();
//        }
        return super.onContextItemSelected(item);
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.d(TAG, "albumid: " + albumid);
//        albumid = requireArguments().getLong("albumid", -1);
//        retrieveSongList();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    public void retrieveSongList(int albumId) {
        Log.v(TAG, "RetrieveSongList");
        if (App.DEBUG) {
            updateSongList(Album.debug().SongList);
            stopRefresh(false);
            return;
        }

        if (!mPresenter.isConnected()) {
            Log.w(TAG, "Not connected when refreshing song list");
            stopRefresh(true);
            return;
        }

        mPresenter.retrieveSongList(albumId);
    }

//    @Override
//    public void onItemClick(View view, int position) {
//        if (isBound) {
//            boundService.musicPlayerStub.play(MediaControl.newBuilder()
//                    .setState(MediaControl.State.PLAY)
//                    .setSongId(songs.get(position).ID)
//                    .build(), boundService.defaultMMPResponseStreamObserver);
//        }
//    }

    @Override
    public void onRefresh() {
        retrieveSongList(albumId);
    }

    public void stopRefresh(boolean hasDelay) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateSongList(ArrayList<Song> songArrayList) {
        Album album = new Album(0, "TESTNAME", false);
        album.SongList = songArrayList;
        mSongAdapter.setAlbum(album);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(TAG, "Unable to update dataset has changed");
            return;
        }
        activity.runOnUiThread(mSongAdapter::notifyDataSetChanged);
    }
}
