package nl.melledijkstra.musicplayerclient.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import nl.melledijkstra.musicplayerclient.R;
import nl.melledijkstra.musicplayerclient.di.ActivityContext;
import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectMPCView;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.MainAdapter;
import nl.melledijkstra.musicplayerclient.ui.main.MainMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.MainMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.MainPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumAdapter;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.controller.ControllerPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongAdapter;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongPresenter;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsMPCView;
import nl.melledijkstra.musicplayerclient.ui.settings.SettingsPresenter;

@Module
public class ActivityModule {
    AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    @PerActivity
    ConnectMPCPresenter<ConnectMPCView> provideConnectPresenter(
            ConnectPresenter<ConnectMPCView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    MainMPCPresenter<MainMPCView> provideMainPresenter(
            MainPresenter<MainMPCView> presenter) {
        return presenter;
    }

    @Provides
    MainAdapter provideMainAdapter() {
        return new MainAdapter(mActivity.getSupportFragmentManager(), mActivity.getLifecycle());
    }

    @Provides
    AlbumMPCPresenter<AlbumMPCView> provideAlbumPresenter(
            AlbumPresenter<AlbumMPCView> presenter) {
        return presenter;
    }

    @Provides
    AlbumAdapter provideAlbumAdapter() {
        return new AlbumAdapter(mActivity.getApplicationContext(), R.layout.album_item);
    }

    @Provides
    ControllerMPCPresenter<ControllerMPCView> provideControllerPresenter(
            ControllerPresenter<ControllerMPCView> presenter) {
        return presenter;
    }

    @Provides
    SongMPCPresenter<SongMPCView> provideSongPresenter(
            SongPresenter<SongMPCView> presenter) {
        return presenter;
    }

    @Provides
    SongAdapter provideSongAdapter() {
        return new SongAdapter();
    }

    @Provides
    @PerActivity
    SettingsMPCPresenter<SettingsMPCView> provideSettingsPresenter(
            SettingsPresenter<SettingsMPCView> presenter) {
        return presenter;
    }
}
