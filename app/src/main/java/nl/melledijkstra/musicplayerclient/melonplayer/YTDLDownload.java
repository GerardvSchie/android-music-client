package nl.melledijkstra.musicplayerclient.melonplayer;

import kotlin.NotImplementedError;
import nl.melledijkstra.musicplayerclient.grpc.MediaDownload;

/**
 * Represents a media download at the server
 */
public class YTDLDownload implements Protoble<MediaDownload> {
    // The states a download can be in
    public enum States {
        DOWNLOADING,
        FINISHED,
        PROCESSING,
    }

    String name;
    String filename;
    String speed;
    String timeElapsed;
    String remainingTime;
    String percentDownloaded;
    String totalBytes;
    States state;

    public YTDLDownload(MediaDownload data) {
        this.Hydrate(data);
    }

    @Override
    public void Hydrate(MediaDownload obj) {
        // TODO
        throw new NotImplementedError("Not implemented");
    }
}
