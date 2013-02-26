/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageOrigin;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.TypeDef;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.api.PoolObjectDropTarget;
import cazcade.vortex.pool.api.PoolObjectPresenterContainer;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenterFactory;
import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractContainerObjectPresenterImpl<T extends PoolObjectView> extends AbstractPoolObjectPresenter<T> implements PoolObjectDropTarget, PoolObjectPresenterContainer {
    @Nonnull
    private final HashMap<LiquidURI, Widget>              poolObjectWidgetsByURI = new HashMap<LiquidURI, Widget>();
    @Nonnull
    private final HashMap<LiquidURI, PoolObjectPresenter> objectPresenters       = new HashMap<LiquidURI, PoolObjectPresenter>();

    public AbstractContainerObjectPresenterImpl(final PoolPresenter pool, @Nonnull final TransferEntity entity, final T widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        BusFactory.get().listen(entity.uri(), RequestType.CREATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message.origin() == LiquidMessageOrigin.SERVER) {
                    try {
                        final TransferEntity requestEntity = message.response();
                        ClientLog.log("Adding " + requestEntity.type().asString());
                        final PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(AbstractContainerObjectPresenterImpl.this, requestEntity, threadSafeExecutor);
                        if (poolObjectPresenter != null) {
                            add(poolObjectPresenter);
                        }

                    } catch (Throwable e) {
                        ClientLog.log(e);
                    }
                }

            }
        });
        BusFactory.get().listen(entity.uri(), RequestType.DELETE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message.origin() == LiquidMessageOrigin.SERVER) {
                    try {
                        final Entity response = message.response();
                        final LiquidURI uri = response.uri();
                        if (objectPresenters.containsKey(uri)) {
                            remove(objectPresenters.get(uri));
                        }

                    } catch (Throwable e) {
                        ClientLog.log(e);
                    }
                }

            }
        });
    }

    @Override
    public void remove(@Nonnull final PoolObjectPresenter presenter) {
        final LiquidURI uri = presenter.getEntity().uri();
        objectPresenters.remove(uri);
        poolObjectWidgetsByURI.remove(uri);
        getPoolObjectView().removeView(presenter.getPoolObjectView());
    }


    @Override
    public boolean willAccept(final TypeDef type) {
        return true;
    }

    @Override
    public void accept(final PoolObjectPresenter source) {
        getPool().transfer(source, this);
    }

    @Override
    public void add(@Nonnull final PoolObjectPresenter presenter) {
        final LiquidURI uri = presenter.getEntity().uri();
        objectPresenters.put(uri, presenter);
        poolObjectWidgetsByURI.put(uri, presenter.getPoolObjectView());
        getPoolObjectView().addView(presenter.getPoolObjectView());
    }
}
