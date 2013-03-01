/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.*;

import java.util.List;


/**
 * @author neilellis@cazcade.com
 */
public interface BusService {

    void start();

    void remove(long listenerId);

    interface UUIDCallback {
        void callback(LiquidUUID uuid);
    }

    void retrieveUUID(final UUIDCallback callback);


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
    <T extends LiquidMessage> void send(T message, MessageCallback<T> callback);

    <T extends LiquidMessage> void send(T message, Callback<T> callback);


    long listen(BusListener listener);

    long listen(LURI uri, BusListener listener);

    long listenForResponses(LURI uri, RequestType type, BusListener listener);

    long listen(LURI uri, RequestType type, BusListener listener);

    <T extends LiquidMessage> long listenForSuccess(LURI uri, RequestType type, BusListener<T> listener);

    long listenForType(LiquidMessageType type, BusListener listener);

    long listenAllBut(List<LiquidMessageType> types, BusListener listener);


    long listenForIdAndType(LURI id, LiquidMessageType type, BusListener listener);

    long listen(LURI id, LiquidMessageType types, BusListener listener);

    long listen(List<LURI> ids, BusListener listener);

    long listen(List<LURI> id, LiquidMessageType type, BusListener listener);

    long listen(List<LURI> ids, List<LiquidMessageType> types, BusListener listener);

    long listenForTypes(List<LiquidMessageType> types, BusListener listener);
}
