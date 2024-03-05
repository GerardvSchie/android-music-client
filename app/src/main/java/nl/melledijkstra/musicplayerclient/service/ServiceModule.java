package nl.melledijkstra.musicplayerclient.service;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    private Context mContext;

    public ServiceModule(Context context) {
        mContext = context;
    }

    @Provides PlayerServiceConnection provideMessagingService() {
        return new AppPlayerService(mContext);
    }
}
