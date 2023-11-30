package nl.melledijkstra.musicplayerclient.ui.main.album;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;

public class AlbumFragment extends BaseFragment implements AlbumMPCView,
        SwipeRefreshLayout.OnRefreshListener,
        GridView.OnItemClickListener {
    static final String TAG = "AlbumsFragment";

    @Inject
    AlbumMPCPresenter<AlbumMPCView> mPresenter;
    @Inject
    AlbumAdapter mAlbumAdapter;

    GridView albumGridView;
    SwipeRefreshLayout swipeRefreshLayout;

    public static AlbumFragment newInstance() {
        Bundle args = new Bundle();
        AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        setUnbinder(ButterKnife.bind(this, view));
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void setUp(View view) {
        requireActivity().setTitle(getString(R.string.albums));

        swipeRefreshLayout = view.requireViewById(R.id.album_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        albumGridView = view.requireViewById(R.id.gv_album_list);
        albumGridView.setOnItemClickListener(this);
        albumGridView.setAdapter(mAlbumAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    public void retrieveAlbumList() {
        Log.v(TAG, "RetrieveAlbumList");
        if (App.DEBUG) {
            updateAlbums(Album.debugList());
            stopRefresh(false);
            return;
        }

        if (!mPresenter.isConnected()) {
            Log.w(TAG, "Not connected when refreshing albums");
            stopRefresh(true);
            return;
        }

        mPresenter.retrieveAlbumList();
    }

    @Override
    public void onRefresh() {
        retrieveAlbumList();
    }

    public void stopRefresh(boolean haveDelay) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Album album = mAlbumAdapter.getItem(position);
        if (album == null) {
            Log.w(TAG, "onItemClick album null");
            return;
        }

        Log.d(TAG, "Clicked on Album: " + album);
//        ((MainActivity) requireActivity()).showSongsFragment(albumModel);
    }

    @Override
    public void updateAlbums(ArrayList<Album> albumList) {
        mAlbumAdapter.setAlbums(albumList);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(TAG, "Unable to update dataset has changed");
            return;
        }
        activity.runOnUiThread(mAlbumAdapter::notifyDataSetChanged);
    }
}
