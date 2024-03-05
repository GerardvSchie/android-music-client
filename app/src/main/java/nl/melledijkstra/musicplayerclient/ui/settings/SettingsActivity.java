package nl.melledijkstra.musicplayerclient.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.ui.base.BaseActivity;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectActivity;
import nl.melledijkstra.musicplayerclient.ui.main.MainActivity;

public class SettingsActivity extends BaseActivity implements SettingsMPCView {
    static final String TAG = "SettingsActivity";
    @Inject
    SettingsMPCPresenter<SettingsMPCView> mPresenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(SettingsActivity.this);
        setUp();
    }

    @Override
    public void openConnectActivity() {
        Intent intent = ConnectActivity.getStartIntent(SettingsActivity.this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(SettingsActivity.this);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();
        fragmentTransaction.add(android.R.id.content, settingsFragment, "SETTINGS_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    protected void onServiceConnected2() {
        // Do nothing
    }

    @Override
    public void onBackPressed() {
        openMainActivity();
    }
}
