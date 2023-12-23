package nl.melledijkstra.musicplayerclient.ui.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.Unbinder;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.di.component.DaggerActivityComponent;
import nl.melledijkstra.musicplayerclient.di.module.ActivityModule;
import nl.melledijkstra.musicplayerclient.service.AppPlayerService;
import nl.melledijkstra.musicplayerclient.service.BaseService;

public abstract class BaseActivity extends AppCompatActivity implements MPCView, BaseFragment.Callback {
    ActivityComponent mActivityComponent;
    BaseService mBaseService;
    protected boolean isBound;
    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(app.getComponent())
                .build();

        Intent intent = AppPlayerService.getStartIntent(this);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public ActivityComponent getActivityComponent() {
        assert mActivityComponent != null : "Activity must be filled";
        return mActivityComponent;
    }

    @Override
    public void onFragmentAttached() {}

    @Override
    public void onFragmentDetached(String tag) {}

    public void setUnBinder(Unbinder unBinder) {
        mUnbinder = unBinder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
            mUnbinder.unbind();
        }
    }

    protected abstract void setUp();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AppPlayerService.LocalBinder binder = (AppPlayerService.LocalBinder) iBinder;
            mBaseService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}
