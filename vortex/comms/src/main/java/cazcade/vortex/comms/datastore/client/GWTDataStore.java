/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.SerializedRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.$;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
@SuppressWarnings("CollectionDeclaredAsConcreteClass")
public class GWTDataStore {

    public static final  int     UNIQUE_ID_CACHE_SIZE = 200;
    public static final  int     MAX_RETRY            = 10;
    //todo: revisit offline behaviour and caching
    private static final boolean SUPPORT_OFFLINE      = false;
    @Nonnull
    private final Bus bus;
    //todo locations!
    @Nonnull
    private final ArrayList<String> locations = new ArrayList<String>();
    private final Runnable onLoggedOutAction;
    @Nonnull
    private final ArrayList<String>        ids                = new ArrayList<String>();
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    @Nonnull
    private SessionIdentifier identity;
    private boolean           collecting;

    public GWTDataStore(@Nonnull final SessionIdentifier newIdentity, @Nonnull final Runnable onStartup, final Runnable onLoggedOutAction) {
        this.onLoggedOutAction = onLoggedOutAction;
        identity = newIdentity;
        bus = BusFactory.get();

        new Timer() {
            @Override
            public void run() {
                if (!collecting) {
                    collecting = true;
                    ClientLog.log("Collection from " + locations);
                    DataStoreService.App.getInstance().collect(identity, locations, new CollectCallback());
                }
            }
        }.scheduleRepeating(1000);

        $.async(new Runnable() {
            @Override public void run() {
                setupListeners(newIdentity);
                onStartup.run();
            }
        });


    }

    private void setupListeners(@Nonnull final SessionIdentifier newIdentity) {
        bus.listen(new AbstractBusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (((LiquidRequest) message).requestType() == RequestType.VISIT_POOL) {
                    final VisitPoolRequest request = (VisitPoolRequest) message;
                    if (request.session().session() == null || request.session().alias().equals(identity.alias())) {
                        if (request.getState() == LiquidMessageState.SUCCESS) {
                            locations.clear();
                            locations.add(newIdentity.alias().asReverseDNSString());
                            final Entity responseEntity = request.response();
                            locations.add(responseEntity.uri().asReverseDNSString() + ".#");
                            locations.add(responseEntity.id().toString());
                        } else if (request.getState() == LiquidMessageState.PROVISIONAL
                                   || request.getState() == LiquidMessageState.INITIAL) {
                            if (request.hasUri()) {
                                locations.add(request.uri().asReverseDNSString() + ".#");
                            }
                            if (request.hasTarget()) {
                                locations.add(request.getTarget().toString());
                            }
                        } else if (request.getState() == LiquidMessageState.FAIL) {
                            if (request.hasUri()) {
                                locations.remove(request.uri().asReverseDNSString() + ".#");
                            }
                            if (request.hasTarget()) {
                                locations.remove(request.getTarget().toString());
                            }

                        }

                    }
                }
            }
        });
        bus.listenAllBut(Arrays.asList(LiquidMessageType.RESPONSE), new AbstractBusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                ClientLog.log("Received a potential message to be stored " + message);
                if (message.origin() == LiquidMessageOrigin.UNASSIGNED && message.messageType() == LiquidMessageType.REQUEST) {
                    ClientLog.log("Storing " + message);
                    //remove the id to allow caching.
                    //                            final LiquidUUID id = message.id();
                    //                            message.id(null);
                    //remove the identity
                    //                            ((LiquidRequest) message).setIdentity(null);
                    ((LiquidRequest) message).session(identity);

                    DataStoreService.App
                                    .getInstance()
                                    .process(message.asSerializedRequest(), new AsyncCallback<SerializedRequest>() {
                                        public int count;

                                        @Override public void onFailure(final Throwable caught) {
                                            ClientLog.log(caught);
                                            if (caught instanceof StatusCodeException && count++ < MAX_RETRY) {
                                                final AsyncCallback<SerializedRequest> callback = this;
                                                new Timer() {
                                                    @Override
                                                    public void run() {
                                                        DataStoreService.App
                                                                        .getInstance()
                                                                        .process(message.asSerializedRequest(), callback);
                                                    }
                                                }.schedule(1000 * count);
                                            }
                                        }

                                        @Override public void onSuccess(@Nullable final SerializedRequest result) {
                                            //                                    result.id(id);
                                            if (result != null) {
                                                final AbstractRequest response = deserializeRequest(result);
                                                bus.dispatch(response);
                                            }
                                        }
                                    });
                }
            }
        });
    }

    @Nonnull
    private static AbstractRequest deserializeRequest(@Nonnull final SerializedRequest result) {
        final AbstractRequest response = result.getType().createInGWT();
        response.setEntity(result.getEntity());
        return response;
    }


    //    private void initCachedDataStore(final String applicationVersion) {
    //        if (SUPPORT_OFFLINE) {
    //            if (Storage.isLocalStorageSupported()) {
    //                //We clear down the storage if the applicationVersion has changed.
    //                final Storage storage = Storage.getLocalStorageIfSupported();
    //                if (storage.getItem(applicationVersion) == null) {
    //                    storage.clear();
    //                    storage.setItem(applicationVersion, "current-version");
    //                }
    //            }
    //            final DataStoreServiceAsync offline = GWT.create(DataStoreService.class);
    //            RpcRequestBuilder builder = new RpcRequestBuilder() {
    //                @Override
    //                protected RequestBuilder doCreate(String serviceEntryPoint) {
    //                    return new OfflineRequestBuilder(identity, RequestBuilder.POST,
    //                            ((ServiceDefTarget) offline).getServiceEntryPoint(), applicationVersion);
    //                }
    //            };
    //            ((ServiceDefTarget) offline).setRpcRequestBuilder(builder);
    //            this.cachedService = offline;
    //        } else {
    //            this.cachedService = DataStoreService.App.getInstance();
    //        }
    //    }

    public void process(@Nonnull final LiquidRequest request, @Nonnull final AsyncCallback<LiquidMessage> callback) {
        request.session(identity);
        if (request.asSerializedRequest().getEntity().type() == null) {
            throw new RuntimeException("Invalid request.");
        }
        DataStoreService.App.getInstance().process(request.asSerializedRequest(), new AsyncCallback<SerializedRequest>() {
            @Override
            public void onFailure(final Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(@Nonnull final SerializedRequest result) {
                final AbstractRequest response = deserializeRequest(result);
                callback.onSuccess(response);
            }
        });
    }

    private boolean unique(final String uniqueId) {
        if (ids.contains(uniqueId)) {
            return false;
        } else {
            threadSafeExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (ids.size() > UNIQUE_ID_CACHE_SIZE) {
                        ids.remove(0);
                    }
                }
            });
            ids.add(uniqueId);
            return true;
        }

    }

    private class CollectCallback implements AsyncCallback<ArrayList<SerializedRequest>> {

        public CollectCallback() {

        }

        public void onFailure(final Throwable throwable) {
            if (throwable instanceof SerializationException) {
                //force refresh of the app.
                new Timer() {
                    @Override
                    public void run() {
                        final String href = Window.Location.getHref();
                        final String nocacheStr = "?nocache=" + System.currentTimeMillis();
                        if (href.contains("?")) {
                            Window.Location.replace(href.replace("?", nocacheStr + "&"));
                        } else if (href.contains("#")) {
                            Window.Location.replace(href.replace("#", nocacheStr + "#"));
                        } else {
                            Window.Location.replace(href + nocacheStr);
                        }
                    }
                }.schedule(10000);
                ClientLog.log(throwable);
                Window.alert(throwable.getMessage());
                //                Window.alert("A new version of the application is now available, this page will reload in 10 seconds, if you have further problems please clear your browsers cache, exit and restart your browser please.");
            } else {

                new Timer() {
                    @Override
                    public void run() {
                        ClientLog.log("Collection from " + locations);

                        DataStoreService.App.getInstance().collect(identity, locations, CollectCallback.this);
                    }
                }.schedule(1000);

                ClientLog.log(throwable);

                if (throwable instanceof LoggedOutException) {
                    identity = null;
                    Window.alert("Logged out");
                    onLoggedOutAction.run();
                }
            }

        }

        public void onSuccess(@Nullable final ArrayList<SerializedRequest> requests) {
            new Timer() {
                @Override
                public void run() {
                    ClientLog.log("Collection from " + locations);
                    DataStoreService.App.getInstance().collect(identity, locations, CollectCallback.this);
                }
            }.schedule(100);
            if (requests != null) {
                ClientLog.log("Result from collect was " + requests.size());
                for (final SerializedRequest serializedRequest : requests) {
                    final AbstractRequest request = deserializeRequest(serializedRequest);
                    ClientLog.log("Dispatching request " + request.id());
                    final String uniqueId = request.deduplicationIdentifier();
                    if (unique(uniqueId)) {
                        bus.dispatch(request);
                    }
                }
            } else {
                ClientLog.log("Result from collect was null.");
            }
        }
    }


}
