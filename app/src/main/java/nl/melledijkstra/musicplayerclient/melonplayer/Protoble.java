package nl.melledijkstra.musicplayerclient.melonplayer;

public interface Protoble<T> {
    /**
     * This method makes sure the object implementing this interface is able to hydrate itself with
     * information from the given source
     * @param obj The protobuf object
     */
    void Hydrate(T obj);
}
