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

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.PlayerState;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.ui.base.BaseFragment;
import nl.melledijkstra.musicplayerclient.utils.AlertUtils;
import nl.melledijkstra.musicplayerclient.utils.MathUtils;
import nl.melledijkstra.musicplayerclient.utils.PlayerTimer;

public class ControllerFragment extends BaseFragment implements ControllerMPCView, AppPlayer.StateUpdateListener, SeekBar.OnSeekBarChangeListener {
    static final String TAG = "ControllerFragment";
    @Inject
    ControllerPresenter<ControllerMPCView> mPresenter;

    SeekBar sbMusicTime, sbVolume;
    ImageButton btnPrev, btnPlayPause, btnNext, btnChangeVolume;
    TextView tvCurrentSong, tvCurPos, tvSongDuration;
    boolean isDragging;
    AlertDialog volumeDialog;

    public static ControllerFragment newInstance() {
        Log.d(TAG, "Creating new instance of ControllerFragment");
        Bundle args = new Bundle();
        ControllerFragment fragment = new ControllerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponent component = getActivityComponent();
        assert component != null : "No service running?";
        component.inject(this);
        mPresenter.onAttach(this);
        mPresenter.registerReceiver();
        mPresenter.registerStateChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_controller, container, false);
        setUnbinder(ButterKnife.bind(this, view));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopTimer();
        mPresenter.unregisterReceiver();
        mPresenter.unRegisterStateChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.startTimer();
        mPresenter.registerReceiver();
        mPresenter.registerStateChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.stopTimer();
        mPresenter.onDetach();
        mPresenter.unregisterReceiver();
        mPresenter.unRegisterStateChangeListener(this);
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
    protected void setUp(View view) {
        sbMusicTime = view.requireViewById(R.id.sbMusicTime);
        sbMusicTime.setOnSeekBarChangeListener(this);

        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.change_volume_dialog, null);
        sbVolume = dialogView.requireViewById(R.id.sbVolume);
        sbVolume.setOnSeekBarChangeListener(onVolumeSeekbarChange);

        btnPrev = view.requireViewById(R.id.btnPreviousSong);
        btnPlayPause = view.requireViewById(R.id.btnPlayPause);
        btnNext = view.requireViewById(R.id.btnNextSong);
        btnChangeVolume = view.requireViewById(R.id.btnChangeVolume);

        tvCurrentSong = view.requireViewById(R.id.tvCurrentSong);
        tvCurPos = view.requireViewById(R.id.tvSongCurPos);
        tvSongDuration = view.requireViewById(R.id.tvSongDuration);

        volumeDialog = AlertUtils.createAlert(getContext(), R.drawable.ic_volume_up, getResources().getString(R.string.change_volume), dialogView)
                .setOnKeyListener((dialog, keyCode, event) -> event.getAction() == KeyEvent.ACTION_DOWN && requireActivity().onKeyDown(keyCode, event)).create();

        btnPlayPause.setOnClickListener(v -> mPresenter.playPause());
        btnPrev.setOnClickListener(v -> mPresenter.previous());
        btnNext.setOnClickListener(v -> mPresenter.next());
        btnChangeVolume.setOnClickListener(v -> {
            if (sbVolume != null) {
                sbVolume.setProgress(mPresenter.appPlayer().Volume);
            }
            volumeDialog.show();
        });
    }

    final SeekBar.OnSeekBarChangeListener onVolumeSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && progress % 3 == 0) {
                mPresenter.changeVolume(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPresenter.changeVolume(seekBar.getProgress());
        }
    };

    @Override
    public void MelonPlayerStateUpdated() {
        Log.d(TAG, "Updated melonplayer state");
        // This call can be invoked from another thread
        // So make sure we are update UI on the UI thread
        AppPlayer appPlayer = mPresenter.appPlayer();
        Log.d(TAG, appPlayer.toString());

        mPresenter.resetTimer();
        updateProgressBar(appPlayer, mPresenter.playerTimer);
        updatePlayPause(appPlayer);

        requireActivity().runOnUiThread(() -> {
            Song currentSong = appPlayer.CurrentSong;
            if (currentSong != null && currentSong.Title != null) {
                tvCurrentSong.setText(currentSong.Title);
                tvCurrentSong.setVisibility(View.VISIBLE);
            } else {
                tvCurrentSong.setText("-");
                tvCurrentSong.setVisibility(View.GONE);
            }

            if (currentSong != null && currentSong.Duration > 0) {
                tvSongDuration.setText(MathUtils.secondsToDurationFormat(currentSong.Duration));
            } else {
                tvSongDuration.setText(R.string.duration_default_text);
            }

            if (sbVolume != null) {
                sbVolume.setProgress(appPlayer.Volume);
            }

            if (appPlayer.State == PlayerState.STOPPED ||
                    appPlayer.State == PlayerState.ENDED) {
                tvCurPos.setText(R.string.duration_default_text);
                tvSongDuration.setText(R.string.duration_default_text);
                tvCurrentSong.setText("-");
                tvCurrentSong.setVisibility(View.GONE);
                sbMusicTime.setProgress(0);
            }
        });
    }

    @Override
    public void updatePlayPause(AppPlayer appPlayer) {
        requireActivity().runOnUiThread(() -> {
            if (appPlayer.State == PlayerState.PLAYING) {
                btnPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
                mPresenter.startTimer();
            } else {
                btnPlayPause.setImageResource(R.drawable.ic_action_playback_play_white);
                mPresenter.stopTimer();
            }
        });
    }

    @Override
    public void updateProgressBar(AppPlayer appPlayer, PlayerTimer playerTimer) {
        if (isDragging) {
            return;
        }

        requireActivity().runOnUiThread(() -> {
            long time = playerTimer.getTime();
            Song song = appPlayer.CurrentSong;

            if (song == null) {
                sbMusicTime.setProgress(0);
            } else {
                int position = (int)(time / (float)(song.Duration) * 100.0);
                sbMusicTime.setProgress(MathUtils.Constrain(position, 0, 100));
            }

            if (time > 0 && !isDragging) {
                tvCurPos.setText(MathUtils.secondsToDurationFormat(time));
            } else {
                tvCurPos.setText(R.string.duration_default_text);
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            Log.d(TAG, "Progressbar changed by user to " + progress);
            Song currentSong = mPresenter.getDataManager().getAppPlayer().CurrentSong;
            if (currentSong == null) {
                return;
            }
            long time = Math.round(currentSong.Duration * (progress / 100f));
            mPresenter.playerTimer.setTime(time);
            tvCurPos.setText(MathUtils.secondsToDurationFormat(time));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDragging = false;
        mPresenter.changePosition(seekBar.getProgress());
    }
}
