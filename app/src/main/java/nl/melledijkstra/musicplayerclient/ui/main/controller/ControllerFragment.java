package nl.melledijkstra.musicplayerclient.ui.main.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;
import nl.melledijkstra.musicplayerclient.utils.AlertUtils;

public class ControllerFragment extends BaseFragment implements AppPlayer.StateUpdateListener {
    static final String TAG = "ControllerFragment";
    SeekBar sbMusicTime, sbVolume;
    ImageButton btnPrev, btnPlayPause, btnNext, btnChangeVolume;
    TextView tvCurrentSong, tvCurPos, tvSongDuration;
    Timer timer;
    boolean isDragging;
    AlertDialog volumeDialog;

    public static ControllerFragment newInstance() {
        Bundle args = new Bundle();
        ControllerFragment fragment = new ControllerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_controller, container, false);
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        setUnbinder(ButterKnife.bind(this, view));
        // TODO: Adapter + presenter??
        return view;
    }

    @Override
    protected void setUp(View view) {
        sbMusicTime = view.requireViewById(R.id.sbMusicTime);
//        sbMusicTime.setOnSeekBarChangeListener(onMusicTimeSeekbarChange);

        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.change_volume_dialog, null);
        sbVolume = dialogView.requireViewById(R.id.sbVolume);
//        sbVolume.setOnSeekBarChangeListener(onVolumeSeekbarChange);

        btnPrev = view.requireViewById(R.id.btnPreviousSong);
        btnPlayPause = view.requireViewById(R.id.btnPlayPause);
        btnNext = view.requireViewById(R.id.btnNextSong);
        btnChangeVolume = view.requireViewById(R.id.btnChangeVolume);

        tvCurrentSong = view.requireViewById(R.id.tvCurrentSong);
        tvCurPos = view.requireViewById(R.id.tvSongCurPos);
        tvSongDuration = view.requireViewById(R.id.tvSongDuration);

        volumeDialog = AlertUtils.createAlert(getContext(), null, String.valueOf(R.string.change_volume), dialogView)
                .setOnKeyListener((dialog, keyCode, event) -> event.getAction() == KeyEvent.ACTION_DOWN && requireActivity().onKeyDown(keyCode, event)).create();

//        btnPlayPause.setOnClickListener(view -> {
//            if (isBound) {
//                boundService.musicPlayerStub.play(MediaControl.newBuilder()
//                        .setState(MediaControl.State.PAUSE).build(), boundService.defaultMMPResponseStreamObserver);
//            }
//        });
//
//        btnPrev.setOnClickListener(view -> {
//            if (isBound) {
//                boundService.musicPlayerStub.previous(PlaybackControl.getDefaultInstance(), boundService.defaultMMPResponseStreamObserver);
//            }
//        });
//
//        btnNext.setOnClickListener(view -> {
//            if (isBound) {
//                boundService.musicPlayerStub.next(PlaybackControl.getDefaultInstance(), boundService.defaultMMPResponseStreamObserver);
//            }
//        });
//
//        btnChangeVolume.setOnClickListener(view -> {
//            if (isBound && sbVolume != null) {
//                sbVolume.setProgress(boundService.getMelonPlayer().Volume);
//            }
//            volumeDialog.show();
//        });
    }
//
//    final SeekBar.OnSeekBarChangeListener onVolumeSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (fromUser && progress % 3 == 0) {
//                if (!isBound) {
//                    return;
//                }
//
////                boundService.musicPlayerStub.changeVolume(VolumeControl.newBuilder()
////                        .setVolumeLevel(progress)
////                        .build(), boundService.defaultMMPResponseStreamObserver);
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            if (!isBound) {
//                return;
//            }
//
////            boundService.musicPlayerStub.changeVolume(VolumeControl.newBuilder()
////                    .setVolumeLevel(seekBar.getProgress())
////                    .build(), boundService.defaultMMPResponseStreamObserver);
//        }
//    };
//
//    final private SeekBar.OnSeekBarChangeListener onMusicTimeSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (fromUser) {
//                Song curSong = boundService.getMelonPlayer().CurrentSong;
//                if (curSong != null) {
//                    long time = Math.round(curSong.Duration * (progress / 100f));
//                    tvCurPos.setText(MathUtils.secondsToDurationFormat(time));
//                }
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            isDragging = true;
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
////            isDragging = false;
////            boundService.musicPlayerStub.changePosition(PositionControl.newBuilder()
////                    .setPosition(seekBar.getProgress())
////                    .build(), boundService.defaultMMPResponseStreamObserver);
//        }
//    };
//
    @Override
    public void MelonPlayerStateUpdated() {
//        // This call can be invoked from another thread
//        // So make sure we are update UI on the UI thread
//        requireActivity().runOnUiThread(() -> {
//            AppPlayer appPlayer = boundService.getMelonPlayer();
//            int curPosition = Math.round(appPlayer.SongPosition);
//            PlayerState playerState = appPlayer.State;
//
//            // Don't update the music time seekbar when user is dragging
//            if (curPosition > 0 && !isDragging) {
//                sbMusicTime.setProgress(curPosition);
//            } else {
//                sbMusicTime.setProgress(0);
//            }
//
//            if (appPlayer.State == PlayerState.PLAYING) {
//                // TODO: make svg animation
//                btnPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
//                startTimerIfPlaying();
//            } else {
//                btnPlayPause.setImageResource(R.drawable.ic_action_playback_play_white);
//                stopTimer();
//            }
//
//            Song currentSong = appPlayer.CurrentSong;
//
//            if (currentSong != null && currentSong.Title != null) {
//                tvCurrentSong.setText(currentSong.Title);
//                tvCurrentSong.setVisibility(View.VISIBLE);
//            } else {
//                tvCurrentSong.setText("-");
//                tvCurrentSong.setVisibility(View.GONE);
//            }
//
//            if (currentSong != null && currentSong.Duration > 0) {
//                tvSongDuration.setText(MathUtils.secondsToDurationFormat(currentSong.Duration));
//            } else {
//                tvSongDuration.setText(R.string.duration_default_text);
//            }
//
//            long elapsedTime = appPlayer.SongElapsedTime;
//            if (elapsedTime > 0 && !isDragging) {
//                tvCurPos.setText(MathUtils.secondsToDurationFormat(elapsedTime));
//            } else {
//                tvCurPos.setText(R.string.duration_default_text);
//            }
//
//            if (sbVolume != null) {
//                sbVolume.setProgress(appPlayer.Volume);
//            }
//
//            if (playerState == PlayerState.STOPPED ||
//                    playerState == PlayerState.ENDED) {
//                tvCurPos.setText(R.string.duration_default_text);
//                tvSongDuration.setText(R.string.duration_default_text);
//                tvCurrentSong.setText("-");
//                tvCurrentSong.setVisibility(View.GONE);
//                sbMusicTime.setProgress(0);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (volumeDialog.isShowing()) {
            volumeDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimerIfPlaying();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopTimer();
    }

    // Starts the timer which updates the song current time progress
    private void startTimerIfPlaying() {
//        if (isBound &&
//                timer == null &&
//                boundService.getMelonPlayer().State == PlayerState.PLAYING) {
//            Log.d(TAG, "Starting status update timer");
//            timer = new Timer(true);
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    if (isBound) {
//                        throw new NotImplementedError("OnBounded");
////                        boundService.retrieveNewStatus();
//                    }
//                }
//            };
//            timer.schedule(timerTask, 0, 950);
//        }
    }

    // Stops the timer progress
    private void stopTimer() {
        if (timer == null) {
            return;
        }

        Log.d(TAG, "Stopping update timer");
        timer.cancel();
        timer = null;
    }
}
