package nl.melledijkstra.musicplayerclient.ui.main.song;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
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
import nl.melledijkstra.musicplayerclient.service.player.model.Album;
import nl.melledijkstra.musicplayerclient.service.player.model.Song;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;

public class SongFragment extends BaseFragment implements SongMPCView, SwipeRefreshLayout.OnRefreshListener, SongAdapter.Callback {
    static final String TAG = "SongsFragment";

    @Inject
    SongMPCPresenter<SongMPCView> mPresenter;
    @Inject
    SongAdapter mSongAdapter;

    RecyclerView songListRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    public static SongFragment newInstance() {
        Log.d(TAG, "Creating new instance of SongFragment");
        Bundle args = new Bundle();
        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        setUnbinder(ButterKnife.bind(this, view));
        mPresenter.onAttach(this);
        mPresenter.connectService(getBaseActivity().mBaseService);
        mSongAdapter.setCallback(this);
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    public void retrieveSongList(Album album) {
        Log.v(TAG, "RetrieveSongList");
        mSongAdapter.setAlbum(album);
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

        mPresenter.retrieveSongList(album);
    }

    @Override
    public void onRefresh() {
        retrieveSongList(mSongAdapter.album());
    }

    public void stopRefresh(boolean hasDelay) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateSongList(ArrayList<Song> songArrayList) {
        mSongAdapter.album().SongList = songArrayList;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(TAG, "Unable to update dataset has changed");
            return;
        }
        activity.runOnUiThread(mSongAdapter::notifyDataSetChanged);
    }

    @Override
    public void playSong(int songId) {
        mPresenter.playSong(songId);
    }

    @Override
    public void addSongNext(int songId) {
        mPresenter.addSongNext(songId);
    }

    @Override
    public void renameSong(int songId, String newTitle) {
        mPresenter.renameSong(songId, newTitle);
    }

    @Override
    public void deleteSong(int songId) {
        mPresenter.deleteSong(songId);
    }

    @Override
    public void moveSong(int songId, int albumId) {
        mPresenter.moveSong(songId, albumId);
    }
}
