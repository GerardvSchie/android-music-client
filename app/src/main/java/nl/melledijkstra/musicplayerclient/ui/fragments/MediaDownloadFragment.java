package nl.melledijkstra.musicplayerclient.ui.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.grpc.stub.StreamObserver;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.grpc.DownloadStatus;
import nl.melledijkstra.musicplayerclient.grpc.MediaDownload;
import nl.melledijkstra.musicplayerclient.melonplayer.AlbumModel;
import nl.melledijkstra.musicplayerclient.melonplayer.YTDLDownload;
import nl.melledijkstra.musicplayerclient.ui.adapters.YoutubeDownloadAdapter;

public class MediaDownloadFragment extends ServiceBoundFragment {

    private static final String TAG = MediaDownloadFragment.class.getSimpleName();

    // UI Components
    FloatingActionButton fabNewDownload;
    ListView listViewDownloadQueue;
    private Unbinder unbinder;

    ArrayList<YTDLDownload> downloadModels;

    YoutubeDownloadAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle("Youtube Downloader");
        downloadModels = new ArrayList<>();
        adapter = new YoutubeDownloadAdapter(getContext(), downloadModels);

        fabNewDownload.setOnClickListener(view -> {
            View dialogLayout = requireActivity().getLayoutInflater().inflate(R.layout.new_download_dialog, null);

            final EditText etYoutubeUrl = dialogLayout.requireViewById(R.id.etYoutubeUrl);
            ClipboardManager clipManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = clipManager != null ? clipManager.getPrimaryClip() : null;
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    if (Patterns.WEB_URL.matcher(item.getText()).matches()) {
                        // TODO: Let user choose to auto fill input
                        etYoutubeUrl.setText(item.getText());
                    }
                }
            }

            final Spinner albumSpinner = dialogLayout.requireViewById(R.id.spinChooseAlbum);

            albumSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, boundService.getMelonPlayer().albumModels));

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.ic_action_youtube)
                    .setView(dialogLayout)
                    .setTitle(R.string.new_download)
                    .setPositiveButton(R.string.download, (dialog1, which) -> {
                        AlbumModel selectedAlbum = ((AlbumModel) albumSpinner.getSelectedItem());
                        if (isBound && selectedAlbum != null) {
                            Log.d(TAG, "Selected album: "+selectedAlbum);
                            boundService.mediaDownloaderStub.downloadMedia(MediaDownload.newBuilder()
                                    .setMediaUrl(etYoutubeUrl.getText().toString())
                                    .setAlbumId(((AlbumModel) albumSpinner.getSelectedItem()).getID())
                                    .build(), new StreamObserver<DownloadStatus>() {
                                @Override
                                public void onNext(DownloadStatus value) {
                                    //downloadModels.add(new YTDLDownload(value));
                                    Log.i(TAG, "onNext: "+value);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    t.printStackTrace();
                                }

                                @Override
                                public void onCompleted() {}
                            });
                        }
                    })
                    .create();

            dialog.show();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_youtube, container, false);
        unbinder = ButterKnife.bind(this, layout);
        listViewDownloadQueue = layout.requireViewById(R.id.listDownloadQueue);
        listViewDownloadQueue.setAdapter(adapter);
        fabNewDownload = layout.requireViewById(R.id.fabNewDownload);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
