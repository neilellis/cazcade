/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.DeletePoolObjectRequest;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.bus.client.Callback;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.api.PoolObjectContainer;
import cazcade.vortex.pool.api.PoolObjectDropTarget;
import cazcade.vortex.pool.api.PoolObjectPresenterContainer;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenterFactory;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class PoolObjectContainerManager {

    @Nonnull
    final HashMap<LURI, Widget>              widgetsByURI     = new HashMap<LURI, Widget>();
    @Nonnull
    final HashMap<LURI, PoolObjectPresenter> objectPresenters = new HashMap<LURI, PoolObjectPresenter>();
    private final long                     createListenerId;
    private final long                     deleteListenerId;
    private       PoolObjectContainer      container;
    private       VortexThreadSafeExecutor executor;
    private       int                      poolObjectCount;
    private       boolean                  destroyed;


    public PoolObjectContainerManager(@Nonnull final PoolObjectContainer container, final VortexThreadSafeExecutor executor, final LURI poolURI) {
        this.container = container;
        this.executor = executor;

        createListenerId = Bus.get().listen(poolURI, RequestType.R_CREATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (destroyed) {
                    return;
                }

                ClientLog.log("Received create pool object request.");
                if (message.origin() == Origin.SERVER) {
                    ClientLog.log("Received create pool object request from server - processing it.");
                    if (message.state() == MessageState.FAIL) {
                        Window.alert("Failed to add pool object.");
                    } else if (message.state() == MessageState.SUCCESS) {
                        try {
                            final TransferEntity response = message.response();
                            ClientLog.log("Adding " + response.type().asString());
                            final PoolObjectPresenter presenter = PoolObjectPresenterFactory.getPresenterForEntity(container, response, executor);
                            if (presenter != null) {
                                add(presenter, true);
                            }

                        } catch (Throwable e) {
                            ClientLog.log(e);
                        }
                    } else {
                        ClientLog.log("Create pool object request was neither success or fail, ignoring.");
                    }

                } else {
                    ClientLog.log("Received create pool object request from server - processing it.");
                }


            }
        });
        deleteListenerId = Bus.get().listen(poolURI, RequestType.R_DELETE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (destroyed) {
                    return;
                }

                if (message.origin() == Origin.SERVER) {
                    if (message.state() == MessageState.FAIL) {
                        Window.alert("Failed to delete pool object.");
                    } else {

                        try {
                            final LURI uri = message.response().uri();
                            if (getObjectPresenters().containsKey(uri)) {
                                remove(getObjectPresenters().get(uri));
                            }

                        } catch (Throwable e) {
                            ClientLog.log(e);
                        }
                    }
                }

            }
        });
    }

    @Nonnull
    private HashMap<LURI, PoolObjectPresenter> getObjectPresenters() {
        return objectPresenters;
    }

    public void add(@Nonnull final PoolObjectPresenter presenter, final boolean centre) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (destroyed) {
                    return;
                }
                final LURI uri = presenter.entity().uri();
                objectPresenters.put(uri, presenter);
                if (widgetsByURI.get(uri) == null) {
                    final Widget view = presenter.view();
                    widgetsByURI.put(uri, view);
                    container.addView(view);
                    presenter.onAddToPool(poolObjectCount++);
                    if (centre) {
                        container.moveToVisibleCentre(presenter);
                    }
                    presenter.setOnDelete(new Runnable() {
                        @Override
                        public void run() {
                            Bus.get().dispatch(new DeletePoolObjectRequest(uri));
                        }
                    });
                }
            }
        });
    }

    void remove(@Nonnull final PoolObjectPresenter poolObjectPresenter) {
        container.removeView(poolObjectPresenter.view());
        final LURI uri = poolObjectPresenter.entity().uri();
        widgetsByURI.remove(uri);
        poolObjectPresenter.onRemoveFromPool();
        objectPresenters.remove(uri);
    }

    public void transfer(@Nonnull final PoolObjectPresenter source, @Nonnull final PoolObjectPresenterContainer dest) {
        source.hide();
        Bus.get()
           .send(new LinkPoolObjectRequest(source.entity().id(), dest.entity().id(), true), new Callback<LinkPoolObjectRequest>() {
               @Override public void handle(LinkPoolObjectRequest message) throws Exception {
                   remove(source);
                   dest.add(source);
               }
           });

    }

    public void checkForCollisions(@Nonnull final PoolObjectPresenter source) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (destroyed) {
                    return;
                }
                final double x = source.getX();
                final double y = source.getY();
                for (final PoolObjectPresenter presenter : objectPresenters.values()) {
                    //noinspection ObjectEquality
                    if (presenter instanceof PoolObjectDropTarget && presenter != source) {
                        final double left = presenter.getLeft();
                        final double right = presenter.getRight();
                        final double top = presenter.getTop();
                        final double bottom = presenter.getBottom();
                        if (x > left && x < right && y > top && y < bottom) {
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (((PoolObjectDropTarget) presenter).willAccept(source.entity().type())) {
                                        ((PoolObjectDropTarget) presenter).accept(source);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void destroy() {
        destroyed = true;
        executor.execute(new Runnable() {
            @Override public void run() {
                container = null;
                executor = null;
                Bus.get().remove(deleteListenerId);
                Bus.get().remove(createListenerId);
                widgetsByURI.clear();
                objectPresenters.clear();
            }
        });
    }
}