package nl.melledijkstra.musicplayerclient.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.grpc.stub.StreamObserver;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.grpc.Album;
import nl.melledijkstra.musicplayerclient.grpc.AlbumList;
import nl.melledijkstra.musicplayerclient.grpc.MediaData;
import nl.melledijkstra.musicplayerclient.melonplayer.AlbumModel;
import nl.melledijkstra.musicplayerclient.ui.MainActivity;
import nl.melledijkstra.musicplayerclient.ui.adapters.AlbumAdapter;

public class AlbumsFragment extends ServiceBoundFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    public static String TAG = "AlbumsFragment";

    GridView albumGridView;
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;

    // The shown albums
    ArrayList<AlbumModel> albumModels;
    AlbumAdapter albumGridAdapter;

    // progress indicator when retrieving albums
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate: Fragment created");
        albumModels = new ArrayList<>();
        albumGridAdapter = new AlbumAdapter(getActivity(), albumModels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(getString(R.string.albums));
        View layout = inflater.inflate(R.layout.fragment_albums, container, false);
        unbinder = ButterKnife.bind(this, layout);

        // get views
        albumGridView = layout.requireViewById(R.id.gv_album_list);
        albumGridView.setAdapter(albumGridAdapter);
        albumGridView.setOnItemClickListener((parent, view, position, id) -> {
            AlbumModel albumModel = (position < albumModels.size()) ? albumModels.get(position) : null;
            Log.i(TAG, "AlbumModel: " + albumModel);
            if (albumModel != null) {
                ((MainActivity) requireActivity()).showSongsFragment(albumModel);
            }
        });

        swipeLayout = layout.requireViewById(R.id.album_swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);

        progressBar = new ProgressBar(requireContext());
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onBounded() {
        retrieveAlbumList();
    }

    private void retrieveAlbumList() {
        if (isBound && boundService != null && boundService.musicPlayerStub != null) {
            progressBar.setVisibility(View.VISIBLE);
            // TODO: move this to service
            boundService.musicPlayerStub.retrieveAlbumList(MediaData.getDefaultInstance(), new StreamObserver<AlbumList>() {
                @Override
                public void onNext(final AlbumList response) {
                    Log.i(TAG, "Retrieved albums, count: " + response.getAlbumListCount());
                    // TODO: find better way to store all album and song data, this is garbage code!!!!
                    albumModels.clear();
                    for (Album album : response.getAlbumListList()) {
                        albumModels.add(new AlbumModel(album));
                    }
                    requireActivity().runOnUiThread(() -> albumGridAdapter.notifyDataSetChanged());
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "grpc onError: ", t);
                }

                @Override
                public void onCompleted() {
                    Log.i(TAG, "onCompleted: album list call done");
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        swipeLayout.setRefreshing(false);
                    });
                }
            });
        } else if (App.DEBUG) {
            Log.i(TAG, "retrieveAlbumList: using debug albums");
            albumModels.clear();
            Collections.addAll(boundService.getMelonPlayer().albumModels,
                    new AlbumModel(0, "Chill", true),
                    new AlbumModel(1, "House", false),
                    new AlbumModel(2, "Classic", true),
                    new AlbumModel(3, "Future House", false),
                    new AlbumModel(4, "Test", false),
                    new AlbumModel(5, "Another AlbumModel", false));
            albumGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        retrieveAlbumList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlbumModel albumModel = (position < albumModels.size()) ? albumModels.get(position) : null;
        Log.i(TAG, "AlbumModel: " + albumModel);
        if (albumModel != null) {
            ((MainActivity) requireActivity()).showSongsFragment(albumModel);
        }
    }
}
