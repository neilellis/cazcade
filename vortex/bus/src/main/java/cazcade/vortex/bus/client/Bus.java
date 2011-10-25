package cazcade.vortex.bus.client;

import cazcade.liquid.api.*;

import java.util.List;


/**
 * @author neilellis@cazcade.com
 */
public interface Bus {

    void start();

    void removeListener(long listenerId);

    public static interface UUIDCallback {
        void callback(LiquidUUID uuid);
    }

    public void retrieveUUID(final UUIDCallback callback);


    /**
     * Dispatch a request, don't callback with the result. This is usually used in conjunction
     * with a listenXXX() method.  This does not get run in the event loop
     * so is safe to call from an event handler.
     *
     * @param message the liquid request.
     * @param <T>     the type of the request.
     * @return the UUID assigned to the request, or the UUID you provided for the request.
     */
    <T extends LiquidMessage> void dispatch(T message);

    /**
     * Send a request, callback with the result, this does not get run in the event loop
     * so is safe to call from an event handler.
     *
     * @param message  the liquid message.
     * @param callback the callback to call on success, fail or exception.
     */
    <T extends LiquidMessage> void send(T message, ResponseCallback<T> callback);

    long listen(BusListener listener);

    long listenForURI(LiquidURI uri, BusListener listener);

    long listenForResponsesForURIAndType(LiquidURI uri, LiquidRequestType type, BusListener listener);

    long listenForURIAndRequestType(LiquidURI uri, LiquidRequestType type, BusListener listener);

    long listenForURIAndSuccessfulRequestType(LiquidURI uri, LiquidRequestType type, BusListener listener);

    long listenForType(LiquidMessageType type, BusListener listener);

    long listenForAllButTheseTypes(List<LiquidMessageType> types, BusListener listener);


    long listenForIdAndType(LiquidURI id, LiquidMessageType type, BusListener listener);

    long listenForIdAndTypes(LiquidURI id, LiquidMessageType types, BusListener listener);

    long listenForIds(List<LiquidURI> ids, BusListener listener);

    long listenForUrisAndType(List<LiquidURI> id, LiquidMessageType type, BusListener listener);

    long listenForIdsAndTypes(List<LiquidURI> ids, List<LiquidMessageType> types, BusListener listener);

    long listenForTypes(List<LiquidMessageType> types, BusListener listener);
}
