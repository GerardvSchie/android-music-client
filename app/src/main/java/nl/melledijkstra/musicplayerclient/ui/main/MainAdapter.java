package nl.melledijkstra.musicplayerclient.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kotlin.NotImplementedError;
import nl.melledijkstra.musicplayerclient.ui.main.album.AlbumFragment;
import nl.melledijkstra.musicplayerclient.ui.main.song.SongFragment;

public class MainAdapter extends FragmentStateAdapter {
    int mCount = 0;

    public MainAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AlbumFragment.newInstance();
            case 1:
                return SongFragment.newInstance();
            default:
                throw new NotImplementedError("Create fragment of songs is not working");
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }
}
