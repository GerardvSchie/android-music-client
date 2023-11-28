package nl.melledijkstra.musicplayerclient.ui.main.album;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;

public class AlbumFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        GridView.OnItemClickListener {
    static final String TAG = "AlbumsFragment";

    GridView albumGridView;
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;

    // The shown albums
    ArrayList<Album> albums = new ArrayList<>();
    AlbumAdapter albumGridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate: Fragment created");
        albumGridAdapter = new AlbumAdapter(getActivity(), albums);
    }

    @Override
    protected void setUp(View view) {
        requireActivity().setTitle(getString(R.string.albums));

        albumGridView = view.requireViewById(R.id.gv_album_list);
        albumGridView.setAdapter(albumGridAdapter);
        albumGridView.setOnItemClickListener((parent, view1, position, id) -> {
            Album album = (position < albums.size()) ? albums.get(position) : null;
            if (album == null) {
                Log.w(TAG, "Didn't click on an album item");
                return;
            }

            Log.d(TAG, "Clicked album " + album);
//            ((MainActivity) requireActivity()).showSongsFragment(albumModel);
        });

        swipeLayout = view.requireViewById(R.id.album_swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        setUnbinder(ButterKnife.bind(this, view));
        // TODO: Adapter + presenter??
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
//
//    @Override
//    protected void onBounded() {
//        retrieveAlbumList();
//    }

    private void retrieveAlbumList() {
//        if (App.DEBUG) {
//            Log.i(TAG, "retrieveAlbumList: using debug albums");
//            albums.clear();
//            Collections.addAll(boundService.getMelonPlayer().Albums,
//                    new Album(0, "Chill", true),
//                    new Album(1, "House", false),
//                    new Album(2, "Classic", true),
//                    new Album(3, "Future House", false),
//                    new Album(4, "Test", false),
//                    new Album(5, "Another AlbumModel", false));
//            albumGridAdapter.notifyDataSetChanged();
//            return;
//        }

//        if (!isBound || boundService == null || boundService.musicPlayerStub == null) {
//            Log.w(TAG, "Cannot retrieve album list");
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        // TODO: move this to service
//        boundService.musicPlayerStub.retrieveAlbumList(MediaData.getDefaultInstance(), new StreamObserver<AlbumList>() {
//            @Override
//            public void onNext(final AlbumList response) {
//                Log.i(TAG, "Retrieved albums, count: " + response.getAlbumListCount());
//                // TODO: find better way to store all album and song data, this is garbage code!!!!
//                albums.clear();
//                for (nl.melledijkstra.musicplayerclient.grpc.Album album : response.getAlbumListList()) {
//                    albums.add(new Album(album));
//                }
//                requireActivity().runOnUiThread(() -> albumGridAdapter.notifyDataSetChanged());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Log.e(TAG, "grpc onError: ", t);
//            }
//
//            @Override
//            public void onCompleted() {
//                Log.i(TAG, "onCompleted: album list call done");
//                requireActivity().runOnUiThread(() -> {
//                    swipeLayout.setRefreshing(false);
//                });
//            }
//        });
//    }
    }

    @Override
    public void onRefresh() {
        retrieveAlbumList();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Album album = (position < albums.size()) ? albums.get(position) : null;
        if (album == null) {
            Log.w(TAG, "onItemClick album null");
            return;
        }

        Log.i(TAG, "AlbumModel: " + album);
//        ((MainActivity) requireActivity()).showSongsFragment(albumModel);
    }
}
