package nl.melledijkstra.musicplayerclient.service;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.service.model.Action;
import nl.melledijkstra.musicplayerclient.service.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.service.player.model.PlayerState;
import nl.melledijkstra.musicplayerclient.service.player.model.Song;

public class PlayerNotificationManager {
    static final String TAG = "PlayerNotificationManager";
    static public final int NOTIFICATION_ID = 955;
    static final String CHANNEL_ID = "nl.melledijkstra.melonmusicplayer.FOREGROUND_NOTIFICATION";
    NotificationManager notificationManager;
    Context context;

    public PlayerNotificationManager(Context context) {
        this.context = context;
        initNotificationManager();
    }

    private void initNotificationManager() {
        notificationManager = getSystemService(context, NotificationManager.class);
        assert notificationManager != null : "No notificationManager present";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Player Channel", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Used to display the notifications of the player");
        notificationManager.createNotificationChannel(channel);
    }

    public void showNotification(AppPlayer appPlayer) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(appPlayer));
    }

    public Notification createNotification(AppPlayer player) {
        Song currentSong = player.CurrentSong;
        if (currentSong == null || currentSong.ID == 0) {
            Log.w(TAG, "No song playing currently to show notification of");
            return createEmptyNotification();
        }

        Log.i(TAG, "Show notification (title=" + currentSong.Title + ")");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(currentSong.Title)
                .setContentText("Context");

        // Add media control buttons that invoke intents in your media service
        notificationBuilder.addAction(R.drawable.ic_skip_previous, "Previous", generatePendingIntent(Action.ACTION_PREV)); // #0
        if (player.State != PlayerState.PLAYING) {
            notificationBuilder.addAction(R.drawable.ic_play_arrow, "Play", generatePendingIntent(Action.ACTION_PLAY_PAUSE)); // #1
        } else {
            notificationBuilder.addAction(R.drawable.ic_pause, "Pause", generatePendingIntent(Action.ACTION_PLAY_PAUSE)); // #1
        }
        notificationBuilder.addAction(R.drawable.ic_skip_next, "Next", generatePendingIntent(Action.ACTION_NEXT)); // #2

        // Apply the media style template
        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2));

        return notificationBuilder.build();
    }

    private Notification createEmptyNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("No song")
                .setContentText("Context");

        return notificationBuilder.build();
    }

    public void removeNotification() {
        Log.d(TAG, "removeNotification");
        notificationManager.cancel(NOTIFICATION_ID);
    }

    // Quick shortcut for creating a pending intent
    private PendingIntent generatePendingIntent(Action action) {
        Intent intent = new Intent(context, NotificationManager.class);
        intent.setAction(action.toString());
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
