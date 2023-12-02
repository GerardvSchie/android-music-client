package nl.melledijkstra.musicplayerclient.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import kotlin.NotImplementedError;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.data.broadcaster.player.model.Album;
import nl.melledijkstra.musicplayerclient.ui.base.BaseActivity;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectActivity;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumFragment;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerFragment;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongFragment;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsActivity;

public class MainActivity extends BaseActivity implements MainMPCView {
    static final String TAG = "MainActivity";
    @Inject
    MainMPCPresenter<MainMPCView> mPresenter;
    @Inject
    MainAdapter mMainAdapter;

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ControllerFragment controllerFragment = ControllerFragment.newInstance();
    AlbumFragment albumFragment;
    SongFragment songFragment;

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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        albumFragment.retrieveAlbumList();
    }

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
            FragmentManager fragmentManager = getSupportFragmentManager();
            int itemId = item.getItemId();
            if (itemId == R.id.drawer_settings) {
                openSettingsActivity();
                return true;
            }

            if (itemId != R.id.drawer_mplayer) {
                Log.w(TAG, "NavItem id not valid");
                return false;
            }

            Fragment fragment = albumFragment;
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.music_content_container, fragment);
            ft.commit();

            drawer.closeDrawers();
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
        } else if (itemId == R.id.action_connect) {
            mPresenter.connect();
        } else {
            Log.w(TAG, "No action for: " + item.getTitle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnect() {
        throw new NotImplementedError("HIDE BUTTON FOR RECONNECT");
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

    public void showAlbumFragment() {
        Log.v(TAG, "Show album fragment");
        getSupportFragmentManager().beginTransaction()
                .show(albumFragment)
                .hide(songFragment)
                .commit();
        setTitle("Albums");
        albumFragment.retrieveAlbumList();
    }

    public void showSongFragment(Album album) {
        Log.v(TAG, "Show songs of album: " + album.Title + " (id=" + album.ID + ")");
        getSupportFragmentManager().beginTransaction()
                .show(songFragment)
                .hide(albumFragment)
                .commit();
        setTitle(album.Title);
        songFragment.retrieveSongList(album);
    }

    @Override
    protected void setUp() {
        Toolbar toolbar = requireViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawer = requireViewById(R.id.main_drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        navigationView = requireViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(onNavigationItemClick);

//        View headerView = navigationView.getHeaderView(0);
//        headerView.setOnClickListener(view -> AlertUtils.createAlert(MainActivity.this, R.mipmap.app_logo, "Melon Music Player", view)
//                .setMessage("The melon music player created by Melle Dijkstra © " + Calendar.getInstance().get(Calendar.YEAR))
//                .show());

        mMainAdapter.setCount(2);
        albumFragment = (AlbumFragment) mMainAdapter.createFragment(0);
        songFragment = (SongFragment) mMainAdapter.createFragment(1);

        // If not all the fragments will be added their onCreateView
        // Hide one and show the other to make sure they don't display over each other
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_content_container, songFragment)
                .add(R.id.music_content_container, albumFragment)
                .show(albumFragment)
                .hide(songFragment)
                .commit();

        controllerFragment = ControllerFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_music_controls, controllerFragment)
                .commit();
    }
}
