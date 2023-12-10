package nl.melledijkstra.musicplayerclient.ui.connect;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.ui.base.BaseActivity;
import nl.melledijkstra.musicplayerclient.ui.main.MainActivity;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsActivity;

public class ConnectActivity extends BaseActivity implements ConnectMPCView {
    static final String TAG = ConnectActivity.class.getSimpleName();
    @Inject
    ConnectMPCPresenter<ConnectMPCView> mPresenter;
    EditText mEditTextIP;
    Button mBtnConnect;
    ProgressBar mProgressBarConnect;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ConnectActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(ConnectActivity.this);
        setUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_connect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openSettingsActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        mPresenter.unregisterReceiver();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        if (mPresenter.isConnected()) {
            Log.i(TAG, "Already connected, opening main activity");
            openMainActivity();
            return;
        }

        mPresenter.registerReceiver();
    }

    public void showLoading() {
        mProgressBarConnect.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBarConnect.setVisibility(View.GONE);
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(ConnectActivity.this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    @Override
    public void openSettingsActivity() {
        Intent intent = SettingsActivity.getStartIntent(ConnectActivity.this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    @Override
    public void failConnection() {
        hideLoading();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        hideLoading();
        if (!mPresenter.isConnected()) {
            Log.i(TAG, "onDestroy: Service is not connected, so stopService is called");
//            stopService(new Intent(this, PlayerService.class));
        }
    }

    @Override
    public void setUp() {
        setSupportActionBar(requireViewById(R.id.toolbar));
        mEditTextIP = requireViewById(R.id.edittext_ip);
        mPresenter.onCreateEditTextIP(mEditTextIP);
        mProgressBarConnect = requireViewById(R.id.progressBar_connect);
        mProgressBarConnect.setIndeterminate(true);
        mProgressBarConnect.setProgressTintList(ColorStateList.valueOf(Color.RED));
        hideLoading();

        mBtnConnect = requireViewById(R.id.button_connect);
        mBtnConnect.requestFocus();
        mBtnConnect.setOnClickListener(this::onConnectClick);
    }

    public void onConnectClick(View v) {
        Log.v(TAG, "OnConnectClick");
        if (App.DEBUG) {
            openMainActivity();
            return;
        }

        showLoading();
        mPresenter.onConnectClick(mEditTextIP.getText().toString());
    }
}
