package nl.melledijkstra.musicplayerclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class AlertUtils {
    private AlertUtils() {
        // Private so we cannot make an alertUtils object
    }

    public static AlertDialog.Builder createAlert(Context context, Integer iconId, String title, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title);
        if (iconId != null) {
            builder.setIcon(iconId);
        }
        if (view != null) {
            builder.setView(view);
        }

        return builder;
    }
}
