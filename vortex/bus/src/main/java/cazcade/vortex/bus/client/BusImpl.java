package cazcade.vortex.bus.client;

import cazcade.liquid.api.*;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

import java.util.*;


/**
 * @author neilellis@cazcade.com
 */
@Singleton
public class BusImpl implements Bus {
    public static final int UUID_BATCH_SIZE = 200;
    public static final int UUID_THRESHOLD = 50;

    private HashMap<LiquidUUID, CallbackProcessor> responseCallbacks = new HashMap<LiquidUUID, CallbackProcessor>();
    private HashMap<String, ListenerCollection> listenerCollections = new HashMap<String, ListenerCollection>();
    private ArrayList<LiquidUUID> uuids = new ArrayList<LiquidUUID>();
    private HashMap<Long, BusListener> listenerLookup = new HashMap<Long, BusListener>();
    public static final Runnable DO_NOTHING = new Runnable() {
        public void run() {
        }
    };

    private boolean started;


    public BusImpl() {
        topUpUUIDs(DO_NOTHING);
    }

    private void topUpUUIDs(final Runnable then) {
        if (uuids.size() < UUID_THRESHOLD) {
            UUIDService.App.getInstance().getRandomUUIDs(UUID_BATCH_SIZE, new AsyncCallback<ArrayList<LiquidUUID>>() {
                public void onFailure(Throwable caught) {
                    ClientLog.log(caught.getMessage(), caught);
                }

                public void onSuccess(ArrayList<LiquidUUID> result) {
                    uuids.addAll(result);
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

    public <T extends LiquidMessage> void dispatch(final T message) {
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
        this.started = true;
    }

    @Override
    public void removeListener(long listenerId) {
        listenerLookup.remove(listenerId);
        for (ListenerCollection collection : listenerCollections.values()) {
            collection.removeListener(listenerId);
        }
    }

    public void retrieveUUID(final UUIDCallback callback) {
        topUpUUIDs(new Runnable() {
            public void run() {
                callback.callback(uuids.remove(0));
            }
        });
    }

    private <T extends LiquidMessage> void assignUUIDThenRun(final T message, final Runnable then) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot assign a UUID to a null message.");
        }
        if (message.getId() == null) {
            topUpUUIDs(new Runnable() {
                public void run() {
                    message.setId(uuids.remove(0));
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

    private <T extends LiquidMessage> void handleCorrelationEvent(LiquidMessage message) {
        LiquidUUID id = message.getId();
        ClientLog.log(responseCallbacks.containsKey(id) ? "Callback found" : "No callback found for " + id);
        ClientLog.log("Message state is " + message.getState());
        ClientLog.log(responseCallbacks.toString());
        if (message.getState() != LiquidMessageState.PROVISIONAL && message.getState() != LiquidMessageState.DEFERRED && message.getState() != LiquidMessageState.INITIAL && responseCallbacks.containsKey(id)) {
            CallbackProcessor responseCallbackProcessor = responseCallbacks.get(id);
            try {
                responseCallbackProcessor.handleResponse(message);
            } catch (Exception e) {
                ClientLog.log(e);
            } finally {
                responseCallbacks.remove(id);
            }
        }
    }

    private <T extends LiquidMessage> void dispatchInternal(T message) {
        if (message.getState() == LiquidMessageState.DEFERRED) {
            return;
        }
        if (!started) {
            throw new RuntimeException("Bus has not been started yet.");
        }
//        ClientLog.log("" + listenerCollections.size());
        List<String> keys = new ArrayList<String>();
        Collection<LiquidURI> affectedEntities = message.getAffectedEntities();
        if (affectedEntities != null) {
            for (LiquidURI affectedEntity : affectedEntities) {
                ClientLog.log("Affected entity: " + affectedEntity);
                keys.add(message.getMessageType().name() + ":" + affectedEntity);
                keys.add("*:" + affectedEntity);
            }
        }
        String responseEntityId = message.getResponse() == null ? "" : message.getResponse().getID().toString();

        keys.add(message.getMessageType().name() + ":" + responseEntityId);
        keys.add(message.getMessageType().name() + ":*");
        keys.add("*:" + responseEntityId);
        keys.add("*:*");
        for (String key : keys) {
//            ClientLog.log("Looking for match for '" + key + "'");
            ListenerCollection listenerCollection = listenerCollections.get(key);
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

    public long listen(BusListener listener) {
        long listenerId = generateId();
        addListener("*:*", listenerId, listener);
        return listenerId;
    }


    private long generateId() {
        return System.currentTimeMillis() * 10000 + ((long) (Math.random() * 10000.0));
    }

    private void addListener(String key, long listenerId, BusListener listener) {
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

    public long listenForAllButTheseTypes(List<LiquidMessageType> types, BusListener listener) {
        long listenerId = generateId();
        for (LiquidMessageType type : LiquidMessageType.values()) {
            if (!types.contains(type)) {
                addListener(type.name() + ":*", listenerId, listener);
            }
        }
        return listenerId;
    }

    public long listenForURI(LiquidURI uri, BusListener listener) {
        long listenerId = generateId();
        addListener("*:" + uri, listenerId, listener);
        return listenerId;
    }

    @Override
    public long listenForResponsesForURIAndType(final LiquidURI uri, final LiquidRequestType type, final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (message instanceof LiquidRequest && message.getOrigin() == LiquidMessageOrigin.SERVER) {
                    ClientLog.log("Not calling " + listener.getClass().getName() + " for " + uri + " as the origin should be " + message.getOrigin() + " when it needs to be SERVER");
                    if (((LiquidRequest) message).getRequestType() == type) {
                        ClientLog.log("Calling " + listener.getClass().getName() + " for " + uri);
                        listener.handle(message);
                    } else {
                        ClientLog.log("Not calling " + listener.getClass().getName() + " for " + uri + " as the type was " + ((LiquidRequest) message).getRequestType() + " and listener wants " + type);
                    }
                }
            }
        });
    }

    @Override
    public long listenForURIAndRequestType(final LiquidURI uri, final LiquidRequestType type, final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (message instanceof LiquidRequest) {
                    if (((LiquidRequest) message).getRequestType() == type) {
                        ClientLog.log("Calling " + listener.getClass().getName() + " for " + uri);
                        listener.handle(message);
                    } else {
                        ClientLog.log("Not calling " + listener.getClass().getName() + " for " + uri + " as the type was " + ((LiquidRequest) message).getRequestType() + " and listener wants " + type);
                    }
                }
            }
        });
    }

    @Override
    public long listenForURIAndSuccessfulRequestType(final LiquidURI uri, final LiquidRequestType type, final BusListener listener) {
        return listenForURI(uri, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (message instanceof LiquidRequest) {
                    if (((LiquidRequest) message).getRequestType() == type && message.getState() == LiquidMessageState.SUCCESS) {
                        listener.handle(message);
                    }
                }
            }
        });
    }


    public long listenForIdAndType(LiquidURI uri, LiquidMessageType type, BusListener listener) {
        return listenForIdsAndTypes(Arrays.asList(uri), Arrays.asList(type), listener);
    }

    public long listenForIdsAndTypes(List<LiquidURI> ids, List<LiquidMessageType> types, BusListener listener) {
        long listenerId = generateId();
        for (LiquidMessageType type : types) {
            for (LiquidURI id : ids) {
                addListener(type.name() + ":" + id, listenerId, listener);
            }
        }
        return listenerId;
    }

    public long listenForIdAndTypes(LiquidURI id, LiquidMessageType types, BusListener listener) {
        return listenForIdsAndTypes(Arrays.asList(id), Arrays.asList(types), listener);
    }

    public long listenForIds(List<LiquidURI> ids, BusListener listener) {
        long listenerId = generateId();
        for (LiquidURI id : ids) {
            addListener("*:" + id, listenerId, listener);
        }
        return listenerId;
    }

    public long listenForUrisAndType(List<LiquidURI> uris, LiquidMessageType type, BusListener listener) {
        return listenForIdsAndTypes(uris, Arrays.asList(type), listener);
    }

    public long listenForType(LiquidMessageType type, BusListener listener) {
        long listenerId = generateId();
        addListener(type + ":*", listenerId, listener);
        return listenerId;
    }

    public long listenForTypes(List<LiquidMessageType> types, BusListener listener) {
        long listenerId = generateId();
        for (LiquidMessageType type : types) {
            addListener(type.name() + ":*", listenerId, listener);
        }
        return listenerId;
    }

    public <T extends LiquidMessage> void send(final T message, final ResponseCallback<T> callback) {
        ClientLog.log("Sending " + message.getClass());
        assignUUIDThenRun(message, new Runnable() {
            public void run() {
                addCallback(message, callback);
                handleCorrelationEvent(message);
                dispatchInternal(message);
            }
        });
    }

    private <T extends LiquidMessage> void addCallback(LiquidMessage message, ResponseCallback<T> callback) {
        LiquidUUID messageId = message.getId();
        ClientLog.log("Adding callback for " + messageId.toString());
        CallbackProcessor callbackProcessor = responseCallbacks.get(messageId);
        if (callbackProcessor == null) {
            callbackProcessor = new CallbackProcessor(message);
            responseCallbacks.put(messageId, callbackProcessor);
        }
        callbackProcessor.addCallback(callback);
    }

    private class ListenerCollection {
        private List<Long> listenerIds = new ArrayList<Long>();

        public void addListener(Long id) {
            listenerIds.add(id);
        }

        public void call(LiquidMessage message) {
            List<Long> deleteThese = new ArrayList<Long>();
            for (Long listenerId : new ArrayList<Long>(listenerIds)) {
                BusListener listener = listenerLookup.get(listenerId);
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

        public void removeListener(long listenerId) {
            listenerIds.remove(listenerId);
        }
    }

    public static class CallbackProcessor {
        private List<ResponseCallback> callbacks = new ArrayList<ResponseCallback>();
        private LiquidMessage message;

        private CallbackProcessor(LiquidMessage message) {
            this.message = message;
        }

        public void addCallback(ResponseCallback callback) {
            callbacks.add(callback);
        }

        public void handleResponse(LiquidMessage response) {
//            ClientLog.log("Callback processor processing " + response.getId());
            for (ResponseCallback responseCallback : callbacks) {
                if (message.getState() == LiquidMessageState.FAIL || (message.getResponse() != null && message.getResponse().isError())) {
                    if (response.getResponse() == null) {
                        ClientLog.log("Callback handling failed and response entity was null. ");
                    } else {
                        ClientLog.log("Callback handling failed " + response.getResponse().asFreeText());
                    }
                    responseCallback.onFailure(message, response);
                } else {
                    if (response.getResponse() == null) {
                        ClientLog.log("Callback handling success, but response entity was null. ");
                    } else {
                        ClientLog.log("Callback handling success " + response.getResponse().asFreeText());
                    }
                    responseCallback.onSuccess(message, response);
                }
            }
        }
    }
}
