package nl.melledijkstra.musicplayerclient.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import kotlin.NotImplementedError;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.ui.base.BaseActivity;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectActivity;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumFragment;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerFragment;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsActivity;
import nl.melledijkstra.musicplayerclient.utils.AlertUtils;

public class MainActivity extends BaseActivity implements MainMPCView {
    static final String TAG = "MainActivity";
    @Inject
    MainMPCPresenter<MainMPCView> mPresenter;

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ControllerFragment controllerFragment = ControllerFragment.newInstance();
    AlbumFragment mAlbumFragment;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);
        setUp();
    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            toggle.syncState();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    NavigationView.OnNavigationItemSelectedListener onNavigationItemClick = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = null;
//            int itemId = item.getItemId();
//            if (itemId == R.id.drawer_mplayer) {
//                if (mAlbumsFragment == null) {
//                    mAlbumsFragment = new AlbumsFragment();
//                }
//                fragment = mAlbumsFragment;
//            } else if (itemId == R.id.drawer_settings) {
//                openSettingsActivity();
//            }
//
//            if (fragment != null) {
//                FragmentTransaction ft = fragmentManager.beginTransaction();
//                ft.replace(R.id.music_content_container, fragment);
//                ft.commit();
//            }
//            drawer.closeDrawers();
            return true;
        }
    };

    @Override
    public void openConnectActivity() {
        Intent intent = ConnectActivity.getStartIntent(MainActivity.this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    @Override
    public void openSettingsActivity() {
        Intent intent = SettingsActivity.getStartIntent(MainActivity.this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    @Override
    public void next() {
        // TODO: Implement what needs to happen to view if next is pressed
        throw new NotImplementedError("TODO IMPLEMENT");
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                if (mBound && mBoundService != null) {
//                    int vol = MathUtils.Constrain(mBoundService.getMelonPlayer().getVolume() + 5, 0, 100);
//                    mBoundService.musicPlayerStub.changeVolume(VolumeControl.newBuilder()
//                            .setVolumeLevel(vol)
//                            .build(), mBoundService.defaultMMPResponseStreamObserver);
//                }
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                if (mBound && mBoundService != null) {
//                    int vol = MathUtils.Constrain(mBoundService.getMelonPlayer().getVolume() - 5, 0, 100);
//                    mBoundService.musicPlayerStub.changeVolume(VolumeControl.newBuilder()
//                            .setVolumeLevel(vol)
//                            .build(), mBoundService.defaultMMPResponseStreamObserver);
//                }
//                return true;
//            default:
//                return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.action_settings) {
            openSettingsActivity();
        } else {
            Log.w(TAG, "No action for: " + item.getTitle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        mPresenter.unregisterReceiver();
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
        if (!mPresenter.isConnected()) {
            Log.d(TAG, "Not connected, going to connection window");
            openConnectActivity();
            return;
        }

        mPresenter.registerReceiver();
    }

//    public void showSongsFragment(AlbumModel albumModel) {
//        SongsFragment songsFragment = new SongsFragment();
//        Bundle bundle = new Bundle();
//        bundle.putLong("albumid", albumModel.getID());
//        Log.v(TAG, "Show songs of albumModel: " + albumModel.getTitle() + " (" + albumModel.getID() + ")");
//        songsFragment.setArguments(bundle);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.music_content_container, songsFragment)
//                .commit();
//        setTitle(albumModel.getTitle());
//    }

    @Override
    protected void setUp() {
        setSupportActionBar(requireViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // DrawerLayout
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer = requireViewById(R.id.main_drawer_layout);
        drawer.addDrawerListener(toggle);

        navigationView = requireViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(onNavigationItemClick);
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(v -> AlertUtils.createAlert(MainActivity.this, R.mipmap.app_logo, "Melon Music Player", null)
                .setMessage("The melon music player created by Melle Dijkstra © " + Calendar.getInstance().get(Calendar.YEAR))
                .show());

        // Start off with a album view
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_content_container, new AlbumFragment())
                .commit();

        controllerFragment = ControllerFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_music_controls, controllerFragment)
                .commit();
    }
}
