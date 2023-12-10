package nl.melledijkstra.musicplayerclient.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.service.player.model.PlayerState;

public class PlayerTimer extends Timer {
    static final String TAG = "PlayerTimer";
    private long currentTime = 0;
    private long stopTime = 0;
    private Timer timer = null;

    public PlayerTimer() {
    }

    public void startTimer(AppPlayer appPlayer, TimerTask timerTask) {
        if (appPlayer == null || appPlayer.CurrentSong == null) {
            Log.w(TAG, "AppPlayer or CurrentSong is null, cannot start timer");
            return;
        } else if (isRunning()) {
            Log.w(TAG, "Cannot start timer, already running");
            return;
        } else if (appPlayer.State != PlayerState.PLAYING) {
            Log.w(TAG, "Player state not playing, not moment to start timer");
            return;
        }

        currentTime = appPlayer.SongElapsedTime;
        stopTime = appPlayer.CurrentSong.Duration;

        Log.d(TAG, "Starting update timer");
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timerTask.run();
                currentTime++;
                if (currentTime >= stopTime) {
                    stopTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stopTimer() {
        if (!isRunning()) {
            return;
        }

        Log.d(TAG, "Stopping update timer");
        timer.cancel();
        timer = null;
    }

    public void resetTimer(AppPlayer appPlayer, TimerTask timerTask) {
        stopTimer();
        startTimer(appPlayer, timerTask);
    }

    public boolean isRunning() {
        return timer != null;
    }

    public long getTime() {
        return currentTime;
    }
    public void setTime(long time) {
        currentTime = time;
    }
}
