package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.*;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class GWTDataStore {

    public static final int UNIQUE_ID_CACHE_SIZE = 200;
    public static final int MAX_RETRY = 10;
    private Bus bus;
    private LiquidSessionIdentifier identity;
    //todo locations!
    private ArrayList<String> locations = new ArrayList<String>();
    private boolean collecting;

    //todo: revisit offline behaviour and caching
    private static final boolean SUPPORT_OFFLINE = false;
    private Runnable onLoggedOutAction;
    private ArrayList<String> ids = new ArrayList<String>();
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    public GWTDataStore(final LiquidSessionIdentifier newIdentity, final Runnable onStartup, Runnable onLoggedOutAction) {
        this.onLoggedOutAction = onLoggedOutAction;
        this.identity = newIdentity;
        bus = BusFactory.getInstance();
        bus.listen(new AbstractBusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (((LiquidRequest) message).getRequestType() == LiquidRequestType.VISIT_POOL) {
                    VisitPoolRequest request = (VisitPoolRequest) message;
                    if (request.getSessionIdentifier() == null || request.getSessionIdentifier().getAlias().equals(identity.getAlias())) {
                        if (request.getState() == LiquidMessageState.SUCCESS) {
                            locations.clear();
                            locations.add(newIdentity.getAlias().asReverseDNSString());
                            locations.add(request.getResponse().getURI().asReverseDNSString() + ".#");
                            locations.add(request.getResponse().getID().toString());
                        } else if (request.getState() == LiquidMessageState.PROVISIONAL || request.getState() == LiquidMessageState.INITIAL) {
                            if (request.getUri() != null) {
                                locations.add(request.getUri().asReverseDNSString() + ".#");
                            }
                            if (request.getTarget() != null) {
                                locations.add(request.getTarget().toString());
                            }
                        } else if (request.getState() == LiquidMessageState.FAIL) {
                            if (request.getUri() != null) {
                                locations.remove(request.getUri().asReverseDNSString() + ".#");
                            }
                            if (request.getTarget() != null) {
                                locations.remove(request.getTarget().toString());
                            }

                        }

                    }
                }
            }
        });
        bus.listenForAllButTheseTypes(Arrays.asList(LiquidMessageType.RESPONSE), new AbstractBusListener() {
            @Override
            public void handle(final LiquidMessage message) {
                ClientLog.log("Received a potential message to be stored " + message);
                if (message.getOrigin() == LiquidMessageOrigin.UNASSIGNED && message.getMessageType() == LiquidMessageType.REQUEST) {
                    ClientLog.log("Storing " + message);
                    //remove the id to allow caching.
//                            final LiquidUUID id = message.getId();
//                            message.setId(null);
                    //remove the identity
//                            ((LiquidRequest) message).setIdentity(null);
                    ((LiquidRequest) message).setIdentity(identity);

                    DataStoreService.App.getInstance().process((LiquidRequest) message, new AsyncCallback<LiquidMessage>() {
                        public int count;

                        public void onFailure(Throwable caught) {
                            ClientLog.log(caught);
                            if (caught instanceof StatusCodeException && count++ < MAX_RETRY) {
                                final AsyncCallback<LiquidMessage> callback = this;
                                new Timer() {
                                    @Override
                                    public void run() {
                                        DataStoreService.App.getInstance().process((LiquidRequest) message, callback);
                                    }
                                }.schedule(1000 * count);
                            }
                        }

                        public void onSuccess(LiquidMessage result) {
//                                    result.setId(id);
                            if (result != null) {
                                bus.dispatch(result);
                            }
                        }
                    });
                }
            }
        });

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
        onStartup.run();

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

    public void process(LiquidRequest request, AsyncCallback<LiquidMessage> callback) {
        request.setIdentity(identity);
        DataStoreService.App.getInstance().process(request, callback);
    }


    private class CollectCallback implements AsyncCallback<ArrayList<LiquidMessage>> {

        public CollectCallback() {

        }

        public void onFailure(Throwable throwable) {
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
                Window.alert("A new version of the application is now available, this page will reload in 10 seconds, if you have further problems please exit and restart your browser please.");
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

        public void onSuccess(ArrayList<LiquidMessage> liquidMessages) {
            new Timer() {
                @Override
                public void run() {
                    ClientLog.log("Collection from " + locations);
                    DataStoreService.App.getInstance().collect(identity, locations, CollectCallback.this);
                }
            }.schedule(100);
            if (liquidMessages != null) {
                ClientLog.log("Result from collect was " + liquidMessages.size());
                for (LiquidMessage message : liquidMessages) {
                    ClientLog.log("Dispatching message " + message.getId());
                    String uniqueId = message.getDeduplicationIdentifier();
                    if (unique(uniqueId)) {
                        bus.dispatch(message);
                    }
                }
            } else {
                ClientLog.log("Result from collect was null.");
            }
        }
    }

    private boolean unique(String uniqueId) {
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


}
