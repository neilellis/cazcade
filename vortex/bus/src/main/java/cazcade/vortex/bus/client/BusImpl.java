/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


/**
 * @author neilellis@cazcade.com
 */
@Singleton
public class BusImpl implements Bus {
    public static final int UUID_BATCH_SIZE = 200;
    public static final int UUID_THRESHOLD  = 50;

    @Nonnull
    private final       HashMap<LiquidUUID, CallbackProcessor> responseCallbacks   = new HashMap<LiquidUUID, CallbackProcessor>();
    @Nonnull
    private final       HashMap<String, ListenerCollection>    listenerCollections = new HashMap<String, ListenerCollection>();
    @Nonnull
    private final       PersistentUUIDService                  uuids               = new PersistentUUIDService();
    @Nonnull
    private final       HashMap<Long, BusListener>             listenerLookup      = new HashMap<Long, BusListener>();
    @Nonnull
    public static final Runnable                               DO_NOTHING          = new Runnable() {
        public void run() {
        }
    };

    private boolean started;


    public BusImpl() {
        topUpUUIDs(DO_NOTHING);
    }

    private void topUpUUIDs(@Nonnull final Runnable then) {
        if (uuids.size() < UUID_THRESHOLD) {

            UUIDService.App.getInstance().getRandomUUIDs(UUID_BATCH_SIZE, new AsyncCallback<ArrayList<LiquidUUID>>() {
                public void onFailure(@Nonnull final Throwable caught) {
                    ClientLog.log(caught.getMessage(), caught);
                }

                public void onSuccess(@Nonnull final ArrayList<LiquidUUID> result) {
                    uuids.topUp(result);
                    then.run();
                }
            });
        } else {
            //Keep all this  out of the event loops.
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    then.run();
                }
            });
        }
    }

    public <T extends LiquidMessage> void dispatch(@Nonnull final T message) {
        assignUUIDThenRun(message, new Runnable() {
            public void run() {
                ClientLog.log("Dispatching " + message.getId());
                handleCorrelationEvent(message);
                dispatchInternal(message);
            }
        });
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void removeListener(final long listenerId) {
        listenerLookup.remove(listenerId);
        for (final ListenerCollection collection : listenerCollections.values()) {
            collection.removeListener(listenerId);
        }
    }

    public void retrieveUUID(@Nonnull final UUIDCallback callback) {
        topUpUUIDs(new Runnable() {
            public void run() {
                callback.callback(uuids.pop());
            }
        });
    }

    private <T extends LiquidMessage> void assignUUIDThenRun(@Nullable final T message, @Nonnull final Runnable then) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot assign a UUID to a null message.");
        }
        if (!message.hasId()) {
            topUpUUIDs(new Runnable() {
                public void run() {
                    message.setId(uuids.pop());
                    ClientLog.log("Assigned UUID " + message.getId());
                    then.run();
                }
            });
        } else {
            //Let's keep all this out of any event loops.
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    then.run();
                }
            });
        }
    }

    private <T extends LiquidMessage> void handleCorrelationEvent(@Nonnull final LiquidMessage message) {
        final LiquidUUID id = message.getId();
        ClientLog.log(responseCallbacks.containsKey(id) ? "Callback found" : "No callback found for " + id);
        ClientLog.log("Message state is " + message.getState());
        ClientLog.log(responseCallbacks.toString());
        if (message.getState() != LiquidMessageState.PROVISIONAL
            && message.getState() != LiquidMessageState.DEFERRED
            && message.getState() != LiquidMessageState.INITIAL
            && responseCallbacks.containsKey(id)) {
            final CallbackProcessor responseCallbackProcessor = responseCallbacks.get(id);
            try {

                responseCallbackProcessor.handleResponse(message);
            } catch (Exception e) {
                ClientLog.log(e);
            } finally {
                responseCallbacks.remove(id);
            }
        }
    }

    private <T extends LiquidMessage> void dispatchInternal(@Nonnull final T message) {
        if (message.getState() == LiquidMessageState.DEFERRED) {
            return;
        }
        if (!started) {
            throw new RuntimeException("Bus has not been started yet.");
        }
        //        ClientLog.log("" + listenerCollections.size());
        final List<String> keys = new ArrayList<String>();
        final Collection<LiquidURI> affectedEntities = message.getAffectedEntities();
        if (affectedEntities != null) {
            for (final LiquidURI affectedEntity : affectedEntities) {
                ClientLog.log("Affected entity: " + affectedEntity);
                keys.add(message.getMessageType().name() + ":" + affectedEntity);
                keys.add("*:" + affectedEntity);
            }
        }
        final String responseEntityId;
        if (message.hasResponseEntity()) {
            responseEntityId = message.getResponse().getUUID().toString();
        } else {
            responseEntityId = "";
        }

        keys.add(message.getMessageType().name() + ":" + responseEntityId);
        keys.add(message.getMessageType().name() + ":*");
        keys.add("*:" + responseEntityId);
        keys.add("*:*");
        for (final String key : keys) {
            //            ClientLog.log("Looking for match for '" + key + "'");
            final ListenerCollection listenerCollection = listenerCollections.get(key);
            if (listenerCollection != null) {
                ClientLog.log("Matched " + key);
                try {
                    listenerCollection.call(message);
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        }
    }

    public long listen(final BusListener listener) {
        final long listenerId = generateId();
        addListener("*:*", listenerId, listener);
        return listenerId;
    }


    private long generateId() {
        return System.currentTimeMillis() * 10000 + (long) (Math.random() * 10000.0);
    }

    private void addListener(final String key, final long listenerId, final BusListener listener) {
        ClientLog.log("Adding listener for '" + key + "' with id of " + listenerId);
        ListenerCollection listenerCollection = listenerCollections.get(key);
        if (listenerCollection == null) {
            listenerCollection = new ListenerCollection();
            listenerCollections.put(key, listenerCollection);
        }
        listenerCollection.addListener(listenerId);
        if (listenerLookup.containsKey(listenerId)) {
            throw new RuntimeException("Id already added: " + listenerId);
        }
        listenerLookup.put(listenerId, listener);
    }

    public long listenForAllButTheseTypes(@Nonnull final List<LiquidMessageType> types, final BusListener listener) {
        final long listenerId = generateId();
        for (final LiquidMessageType type : LiquidMessageType.values()) {
            if (!types.contains(type)) {
                addListener(type.name() + ":*", listenerId, listener);
            }
        }
        return listenerId;
    }

    public long listenForURI(final LiquidURI uri, final BusListener listener) {
        final long listenerId = generateId();
        addListener("*:" + uri, listenerId, listener);
        return listenerId;
    }

    @Override
    public long listenForResponsesForURIAndType(final LiquidURI uri, final LiquidRequestType type, @Nonnull final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message instanceof LiquidRequest && message.getOrigin() == LiquidMessageOrigin.SERVER) {
                    ClientLog.log("Not calling "
                                  + listener.getClass().getName()
                                  + " for "
                                  + uri
                                  + " as the origin should be "
                                  + message.getOrigin()
                                  + " when it needs to be SERVER");
                    if (((LiquidRequest) message).getRequestType() == type) {
                        ClientLog.log("Calling " + listener.getClass().getName() + " for " + uri);
                        listener.handle(message);
                    } else {
                        ClientLog.log("Not calling "
                                      + listener.getClass().getName()
                                      + " for "
                                      + uri
                                      + " as the type was "
                                      + ((LiquidRequest) message).getRequestType()
                                      + " and listener wants "
                                      + type);
                    }
                }
            }
        });
    }

    @Override
    public long listenForURIAndRequestType(final LiquidURI uri, final LiquidRequestType type, @Nonnull final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message instanceof LiquidRequest) {
                    if (((LiquidRequest) message).getRequestType() == type) {
                        ClientLog.log("Calling " + listener.getClass().getName() + " for " + uri);
                        listener.handle(message);
                    } else {
                        ClientLog.log("Not calling "
                                      + listener.getClass().getName()
                                      + " for "
                                      + uri
                                      + " as the type was "
                                      + ((LiquidRequest) message).getRequestType()
                                      + " and listener wants "
                                      + type);
                    }
                }
            }
        });
    }

    @Override
    public long listenForURIAndSuccessfulRequestType(final LiquidURI uri, final LiquidRequestType type, @Nonnull final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message instanceof LiquidRequest) {
                    if (((LiquidRequest) message).getRequestType() == type && message.getState() == LiquidMessageState.SUCCESS) {
                        listener.handle(message);
                    }
                }
            }
        });
    }


    public long listenForIdAndType(final LiquidURI uri, final LiquidMessageType type, final BusListener listener) {
        return listenForIdsAndTypes(Arrays.asList(uri), Arrays.asList(type), listener);
    }

    public long listenForIdsAndTypes(@Nonnull final List<LiquidURI> ids, @Nonnull final List<LiquidMessageType> types, final BusListener listener) {
        final long listenerId = generateId();
        for (final LiquidMessageType type : types) {
            for (final LiquidURI id : ids) {
                addListener(type.name() + ":" + id, listenerId, listener);
            }
        }
        return listenerId;
    }

    public long listenForIdAndTypes(final LiquidURI id, final LiquidMessageType types, final BusListener listener) {
        return listenForIdsAndTypes(Arrays.asList(id), Arrays.asList(types), listener);
    }

    public long listenForIds(@Nonnull final List<LiquidURI> ids, final BusListener listener) {
        final long listenerId = generateId();
        for (final LiquidURI id : ids) {
            addListener("*:" + id, listenerId, listener);
        }
        return listenerId;
    }

    public long listenForUrisAndType(@Nonnull final List<LiquidURI> uris, final LiquidMessageType type, final BusListener listener) {
        return listenForIdsAndTypes(uris, Arrays.asList(type), listener);
    }

    public long listenForType(final LiquidMessageType type, final BusListener listener) {
        final long listenerId = generateId();
        addListener(type + ":*", listenerId, listener);
        return listenerId;
    }

    public long listenForTypes(@Nonnull final List<LiquidMessageType> types, final BusListener listener) {
        final long listenerId = generateId();
        for (final LiquidMessageType type : types) {
            addListener(type.name() + ":*", listenerId, listener);
        }
        return listenerId;
    }

    public <T extends LiquidMessage> void send(@Nonnull final T message, final ResponseCallback<T> callback) {
        ClientLog.log("Sending " + message.getClass());
        assignUUIDThenRun(message, new Runnable() {
            public void run() {
                addCallback(message, callback);
                handleCorrelationEvent(message);
                dispatchInternal(message);
            }
        });
    }

    private <T extends LiquidMessage> void addCallback(@Nonnull final LiquidMessage message, final ResponseCallback<T> callback) {
        final LiquidUUID messageId = message.getId();
        ClientLog.log("Adding callback for " + messageId.toString());
        CallbackProcessor callbackProcessor = responseCallbacks.get(messageId);
        if (callbackProcessor == null) {
            callbackProcessor = new CallbackProcessor(message);
            responseCallbacks.put(messageId, callbackProcessor);
        }
        callbackProcessor.addCallback(callback);
    }

    private class ListenerCollection {
        @Nonnull
        private final List<Long> listenerIds = new ArrayList<Long>();

        public void addListener(final Long id) {
            listenerIds.add(id);
        }

        public void call(final LiquidMessage message) {
            final List<Long> deleteThese = new ArrayList<Long>();
            for (final Long listenerId : new ArrayList<Long>(listenerIds)) {
                final BusListener listener = listenerLookup.get(listenerId);
                if (listener == null) {
                    ClientLog.log("Removing stale listener " + listenerId);
                    deleteThese.add(listenerId);
                } else {
                    try {
                        listener.handle(message);
                    } catch (Exception e) {
                        ClientLog.log(e);
                    }
                }
            }
            listenerIds.removeAll(deleteThese);
        }

        public void removeListener(final long listenerId) {
            listenerIds.remove(listenerId);
        }
    }

    public static class CallbackProcessor {
        @Nonnull
        private final List<ResponseCallback> callbacks = new ArrayList<ResponseCallback>();
        private final LiquidMessage message;

        private CallbackProcessor(final LiquidMessage message) {
            this.message = message;
        }

        public void addCallback(final ResponseCallback callback) {
            callbacks.add(callback);
        }

        public void handleResponse(@Nonnull final LiquidMessage response) {
            //            ClientLog.log("Callback processor processing " + response.getId());
            for (final ResponseCallback responseCallback : callbacks) {
                if (message.getState() == LiquidMessageState.FAIL || message.hasResponseEntity() && message.getResponse()
                                                                                                           .isError()) {
                    final LSDBaseEntity responseEntity = response.getResponse();
                    ClientLog.log("Callback handling failed " + responseEntity.asDebugText());
                    responseCallback.onFailure(message, response);
                } else {
                    final LSDBaseEntity responseEntity = response.getResponse();
                    ClientLog.log("Callback handling success " + responseEntity.asDebugText());
                    responseCallback.onSuccess(message, response);
                }
            }
        }
    }
}
