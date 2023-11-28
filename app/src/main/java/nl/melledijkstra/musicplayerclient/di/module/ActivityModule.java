package nl.melledijkstra.musicplayerclient.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import nl.melledijkstra.musicplayerclient.di.ActivityContext;
import nl.melledijkstra.musicplayerclient.di.PerActivity;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectMPCView;
import nl.melledijkstra.musicplayerclient.ui.connect.ConnectPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.MainMPCPresenter;
import nl.melledijkstra.musicplayerclient.ui.main.MainMPCView;
import nl.melledijkstra.musicplayerclient.ui.main.MainPresenter;
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
    @PerActivity
    SettingsMPCPresenter<SettingsMPCView> provideSettingsPresenter(
            SettingsPresenter<SettingsMPCView> presenter) {
        return presenter;
    }
}
