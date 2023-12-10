package nl.melledijkstra.musicplayerclient.ui.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.Unbinder;
import nl.melledijkstra.musicplayerclient.App;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;
import nl.melledijkstra.musicplayerclient.di.component.DaggerActivityComponent;
import nl.melledijkstra.musicplayerclient.di.module.ActivityModule;
import nl.melledijkstra.musicplayerclient.di.module.ServiceModule;
import nl.melledijkstra.musicplayerclient.service.AppPlayerService;

public abstract class BaseActivity extends AppCompatActivity implements MPCView, BaseFragment.Callback {
    ActivityComponent mActivityComponent;
    Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .serviceModule(new ServiceModule(new AppPlayerService()))
                .applicationComponent(((App) getApplication()).getComponent())
                .build();
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
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract void setUp();
}
