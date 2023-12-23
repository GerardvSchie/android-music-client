package nl.melledijkstra.musicplayerclient.ui.base;

import nl.melledijkstra.musicplayerclient.data.DataManager;

public class BasePresenter<V extends MPCView> implements MPCPresenter<V> {
    final public DataManager mDataManager;

    protected V mView;

    public BasePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void onAttach(V mvpView) {
        mView = mvpView;
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    public boolean isViewAttached() {
        return mView != null;
    }

    public V getIView() {
        return mView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new IViewNotAttachedException();
        }
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public static class IViewNotAttachedException extends RuntimeException {
        public IViewNotAttachedException() {
            super("Please call Presenter.onAttach(MPCView) before" +
                    " requesting data to the Presenter");
        }
    }
}
