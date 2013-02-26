/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.DeletePoolObjectRequest;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
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
    final HashMap<LiquidURI, Widget>              poolObjectWidgetsByURI = new HashMap<LiquidURI, Widget>();
    @Nonnull
    final HashMap<LiquidURI, PoolObjectPresenter> objectPresenters       = new HashMap<LiquidURI, PoolObjectPresenter>();
    private final long                     createListenerId;
    private final long                     deleteListenerId;
    private       PoolObjectContainer      container;
    private       VortexThreadSafeExecutor executor;
    private       int                      poolObjectCount;
    private       boolean                  destroyed;


    public PoolObjectContainerManager(@Nonnull final PoolObjectContainer container, final VortexThreadSafeExecutor executor, final LiquidURI poolURI) {
        this.container = container;
        this.executor = executor;

        createListenerId = BusFactory.get().listen(poolURI, RequestType.CREATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (destroyed) {
                    return;
                }

                ClientLog.log("Received create pool object request.");
                if (message.origin() == LiquidMessageOrigin.SERVER) {
                    ClientLog.log("Received create pool object request from server - processing it.");
                    if (message.getState() == LiquidMessageState.FAIL) {
                        Window.alert("Failed to add pool object.");
                    } else if (message.getState() == LiquidMessageState.SUCCESS) {
                        try {
                            final TransferEntity requestEntity = message.response();
                            ClientLog.log("Adding " + requestEntity.type().asString());
                            final PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(container, requestEntity, executor);
                            if (poolObjectPresenter != null) {
                                add(poolObjectPresenter, true);
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
        deleteListenerId = BusFactory.get().listen(poolURI, RequestType.DELETE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (destroyed) {
                    return;
                }

                if (message.origin() == LiquidMessageOrigin.SERVER) {
                    if (message.getState() == LiquidMessageState.FAIL) {
                        Window.alert("Failed to delete pool object.");
                    } else {

                        try {
                            final Entity response = message.response();
                            final LiquidURI uri = response.uri();
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
    private HashMap<LiquidURI, PoolObjectPresenter> getObjectPresenters() {
        return objectPresenters;
    }

    public void add(@Nonnull final PoolObjectPresenter poolObjectPresenter, final boolean centre) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (destroyed) {
                    return;
                }
                final LiquidURI uri = poolObjectPresenter.getEntity().uri();
                objectPresenters.put(uri, poolObjectPresenter);
                if (poolObjectWidgetsByURI.get(uri) == null) {
                    final Widget view = poolObjectPresenter.getPoolObjectView();
                    poolObjectWidgetsByURI.put(uri, view);
                    container.addView(view);
                    poolObjectPresenter.onAddToPool(poolObjectCount++);
                    if (centre) {
                        //                        container.moveToVisibleCentre(poolObjectPresenter);
                    }
                    poolObjectPresenter.setOnDelete(new Runnable() {
                        @Override
                        public void run() {
                            BusFactory.get()
                                      .send(new DeletePoolObjectRequest(uri), new AbstractResponseCallback<DeletePoolObjectRequest>() {
                                          @Override
                                          public void onSuccess(final DeletePoolObjectRequest message, final DeletePoolObjectRequest response) {
                                              //                                    executor.execute(new Runnable() {
                                              //                                        @Override
                                              //                                        public void run() {
                                              //                                            remove(poolObjectPresenter);
                                              //                                        }
                                              //                                    });
                                          }
                                      });
                        }
                    });
                }
            }
        });
    }

    void remove(@Nonnull final PoolObjectPresenter poolObjectPresenter) {
        container.removeView(poolObjectPresenter.getPoolObjectView());
        final LiquidURI uri = poolObjectPresenter.getEntity().uri();
        poolObjectWidgetsByURI.remove(uri);
        poolObjectPresenter.onRemoveFromPool();
        objectPresenters.remove(uri);
    }

    public void transfer(@Nonnull final PoolObjectPresenter source, @Nonnull final PoolObjectPresenterContainer destination) {
        source.hide();
        BusFactory.get()
                  .send(new LinkPoolObjectRequest(source.getEntity().id(), destination.getEntity()
                                                                                      .id(), true), new AbstractResponseCallback<LinkPoolObjectRequest>() {
                      @Override
                      public void onSuccess(final LinkPoolObjectRequest message, final LinkPoolObjectRequest response) {
                          remove(source);
                          destination.add(source);
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
                                    if (((PoolObjectDropTarget) presenter).willAccept(source.getEntity().type())) {
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
                BusFactory.get().remove(deleteListenerId);
                BusFactory.get().remove(createListenerId);
                poolObjectWidgetsByURI.clear();
                objectPresenters.clear();
            }
        });
    }
}