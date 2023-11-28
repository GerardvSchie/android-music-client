package nl.melledijkstra.musicplayerclient.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import nl.melledijkstra.musicplayerclient.R;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }
}