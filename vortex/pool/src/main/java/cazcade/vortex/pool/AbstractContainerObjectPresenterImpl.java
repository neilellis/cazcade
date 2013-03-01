/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.Origin;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.TypeDef;
import cazcade.vortex.bus.client.Bus;
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

import static cazcade.liquid.api.RequestType.*;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractContainerObjectPresenterImpl<T extends PoolObjectView> extends AbstractPoolObjectPresenter<T> implements PoolObjectDropTarget, PoolObjectPresenterContainer {
    @Nonnull
    private final HashMap<LURI, Widget>              poolObjectWidgetsByURI = new HashMap<LURI, Widget>();
    @Nonnull
    private final HashMap<LURI, PoolObjectPresenter> objectPresenters       = new HashMap<LURI, PoolObjectPresenter>();

    public AbstractContainerObjectPresenterImpl(final PoolPresenter pool, @Nonnull final TransferEntity entity, final T widget, final VortexThreadSafeExecutor executor) {
        super(pool, entity, widget, executor);
        Bus.get().listen(entity.uri(), R_CREATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message.origin() == Origin.SERVER) {
                    try {
                        final TransferEntity response = message.response();
                        ClientLog.log("Adding " + response.type());
                        final PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(AbstractContainerObjectPresenterImpl.this, response, executor);
                        if (poolObjectPresenter != null) {
                            add(poolObjectPresenter);
                        }

                    } catch (Throwable e) {
                        ClientLog.log(e);
                    }
                }

            }
        });
        Bus.get().listen(entity.uri(), R_DELETE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(@Nonnull final LiquidMessage message) {
                if (message.origin() == Origin.SERVER) {
                    try {
                        final LURI uri = message.response().uri();
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
        final LURI uri = presenter.entity().uri();
        objectPresenters.remove(uri);
        poolObjectWidgetsByURI.remove(uri);
        view().removeView(presenter.view());
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
        final LURI uri = presenter.entity().uri();
        objectPresenters.put(uri, presenter);
        poolObjectWidgetsByURI.put(uri, presenter.view());
        view().add(presenter.view());
    }
}
