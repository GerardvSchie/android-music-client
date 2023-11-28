package nl.melledijkstra.musicplayerclient.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.AppPlayer;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Song;

public final class NotificationUtils {
    static final String TAG = "NotificationUtils";

    static final int NOTIFICATION_ID = 955;
    static final String NOTIFICATION_ID_STRING = "955";
    static final String CHANNEL_ID = "nl.melledijkstra.melonmusicplayer.FOREGROUND_NOTIFICATION";

    private NotificationUtils() {
        // This utility class is not publicly instantiable
    }

    public static NotificationManager createNotificationManager(Context context) {
        NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
        assert notificationManager != null : "No notificationManager present";

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID_STRING, "TESTNOTIFICATIONNAME", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("TestDescription");
        notificationManager.createNotificationChannel(channel);
        return notificationManager;
    }

    public static void showNotification(Context context, NotificationManager notificationManager, AppPlayer player) {
        Song currentSong = player.CurrentSong;
        if (currentSong == null || currentSong.ID == 0) {
            Log.w(TAG, "No song playing currently to show notification of");
            return;
        }

        Log.i(TAG, "Show notification (title=" + currentSong.Title + ")");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.notification_icon)
                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.ic_skip_previous, "Previous", generatePendingIntent(MelonPlayerService.ACTION_PREV)) // #0
//                .addAction(R.drawable.ic_pause, "Pause", generatePendingIntent(MelonPlayerService.ACTION_PLAY_PAUSE))  // #1
//                .addAction(R.drawable.ic_skip_next, "Next", generatePendingIntent(MelonPlayerService.ACTION_NEXT))     // #2
                // Apply the media style template.
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */))
                .setContentTitle(currentSong.Title)
                .setContentText("Context");

        // notificationId is a unique int for each notification that you must define.
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
//        if (state != null) {
//            if (state == MelonPlayer.States.PLAYING) {
//                // TODO: set to pause button
//                //notificationBuilder.addAction(R.drawable.ic_pause, "Pause", generatePendingIntent(ACTION_PLAY_PAUSE));
//            } else {
//                // TODO: set to play button
//                //notificationBuilder.addAction(R.drawable.ic_play_arrow, "Play", generatePendingIntent(ACTION_PLAY_PAUSE));
//            }
//        }
    }

    public static void removeNotification(NotificationManager notificationManager) {
        if (notificationManager != null) {
            Log.i(TAG, "removeNotification");
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

//    // Quick shortcut for creating a pending intent
//    private static PendingIntent generatePendingIntent(String action) {
//        Intent intent = new Intent(this, MelonPlayerService.class);
//        intent.setAction(action);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//    }
}
