package nl.melledijkstra.musicplayerclient.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import butterknife.ButterKnife;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.MelonPlayerService;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.config.Constants;
import nl.melledijkstra.musicplayerclient.config.PreferenceKeys;

public class ConnectActivity extends AppCompatActivity {
    static final String TAG = ConnectActivity.class.getSimpleName();

    // Application specific settings
    SharedPreferences mSettings;

    // UI Components
    EditText mEditTextIP;
    Button mBtnConnect;
    ProgressBar mProgressBarConnect;

    boolean mReceiverRegistered = false;

    // The connection service
    public MelonPlayerService mBoundService;
    // Variable for checking if service is bound
    boolean mBound = false;

    IntentFilter mBroadcastFilter;
    LocalBroadcastManager mBroadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);

        startService(new Intent(this, MelonPlayerService.class));
        bindService(new Intent(this, MelonPlayerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        mBroadcastManager = LocalBroadcastManager.getInstance(this);

        mBroadcastFilter = new IntentFilter();
        mBroadcastFilter.addAction(MelonPlayerService.READY);
        mBroadcastFilter.addAction(MelonPlayerService.DISCONNECTED);
        mBroadcastFilter.addAction(MelonPlayerService.CONNECTFAILED);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        createUI();
    }

    public void createUI() {
        setSupportActionBar(requireViewById(R.id.toolbar));

        mEditTextIP = requireViewById(R.id.edittext_ip);
        mEditTextIP.setText(mSettings.getString(PreferenceKeys.HOST_IP, Constants.DEFAULT_IP));

        mProgressBarConnect = requireViewById(R.id.progressBar_connect);
        mBtnConnect = requireViewById(R.id.button_connect);
        mBtnConnect.requestFocus();
        mBtnConnect.setOnClickListener(view -> {
            if (App.DEBUG) {
                startMainScreen();
                return;
            }

            final String ip = mEditTextIP.getText().toString();

            // Save the data
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(PreferenceKeys.HOST_IP, ip);
            editor.apply();
            Log.v(TAG, "IP Saved to preferences (" + ip + ")");

            // send broadcast to service that it needs to try connecting
            LocalBroadcastManager.getInstance(ConnectActivity.this).sendBroadcast(new Intent(MelonPlayerService.INITIATE_CONNECTION));

            mProgressBarConnect.setProgressTintList(ColorStateList.valueOf(Color.RED));
            mProgressBarConnect.setVisibility(View.VISIBLE);
        });

        mProgressBarConnect.setVisibility(View.GONE);
        mProgressBarConnect.setIndeterminate(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_connect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent startSettings = new Intent(ConnectActivity.this, SettingsActivity.class);
            startActivity(startSettings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        unregisterBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if (mBoundService != null && mBoundService.isConnected()) {
            Log.v(TAG, "Already connected, opening main activity");
            startMainScreen();
        } else {
            Log.v(TAG, "Not connected, staying in connect activity");
            registerBroadcastReceiver();
        }
    }

    /**
     * Broadcast receiver which gets notified of events in the service
     */
    final private BroadcastReceiver bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "BROADCAST RECEIVED: " + intent.getAction());
            String action = intent.getAction();
            assert action != null;

            switch (action) {
                case MelonPlayerService.READY:
                    // If service says it's connected then open MainActivity
                    if (mProgressBarConnect.isShown()) {
                        mProgressBarConnect.setVisibility(View.GONE);
                    }
                    startMainScreen();
                    break;
                case MelonPlayerService.CONNECTFAILED:
                    if (mProgressBarConnect.isShown()) {
                        mProgressBarConnect.setVisibility(View.GONE);
                    }
                    String reason = (intent.getStringExtra("state") != null) ? ": " + intent.getStringExtra("state") : "";
                    Toast.makeText(ConnectActivity.this, "Connect failed" + reason, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, mBroadcastFilter);
        Log.v(TAG, "Broadcast listener registered");
        mReceiverRegistered = true;
    }

    private void unregisterBroadcastReceiver() {
        if (mReceiverRegistered && bReceiver != null) {
            mBroadcastManager.unregisterReceiver(bReceiver);
            mReceiverRegistered = false;
            Log.v(TAG, "Broadcast listener unregistered");
        }
    }

    final private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.i(TAG, "onServiceConnected");
            MelonPlayerService.LocalBinder myBinder = (MelonPlayerService.LocalBinder) binder;
            mBoundService = myBinder.getService();
            mBound = true;
            if(mBoundService != null && mBoundService.isConnected()) {
                Log.i(TAG, "service is connected, start main screen...");
                startMainScreen();
            } else {
                Log.i(TAG, "checkConnection: service is not connected, do nothing");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mBound = false;
        }
    };

    /**
     * Transitions from Connection activity to MainActivity
     */
    private void startMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, getClass().getSimpleName() + " - onDestroy");
        if(!mBoundService.isConnected()) {
            Log.i(TAG, "onDestroy: Service is not connected, so stopService is called");
            stopService(new Intent(this, MelonPlayerService.class));
        }
        unregisterBroadcastReceiver();
        if(mProgressBarConnect.isShown()) {
            mProgressBarConnect.setVisibility(View.GONE);
        }
        if (mServiceConnection != null && mBound) {
            unbindService(mServiceConnection);
            Log.v(TAG, "Unbinding service");
        }
    }
}
