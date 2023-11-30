package nl.melledijkstra.musicplayerclient.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.Unbinder;
import nl.melledijkstra.musicplayerclient.di.component.ActivityComponent;

public abstract class BaseFragment extends Fragment implements MPCView {
    static final String TAG = "BaseFragment";
    BaseActivity mActivity;
    Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof BaseActivity)) {
            Log.w(TAG, "onAttach was not an instance of BaseActivity");
            return;
        }

        BaseActivity activity = (BaseActivity) context;
        this.mActivity = activity;
        activity.onFragmentAttached();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public ActivityComponent getActivityComponent() {
        if (mActivity == null) {
            return null;
        }
        return mActivity.getActivityComponent();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }

    protected abstract void setUp(View view);

    public interface Callback {
        void onFragmentAttached();
        void onFragmentDetached(String tag);
    }
}
