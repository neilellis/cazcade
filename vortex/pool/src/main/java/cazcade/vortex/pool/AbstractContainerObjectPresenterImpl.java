package cazcade.vortex.pool;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageOrigin;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDTypeDef;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.api.PoolObjectDropTarget;
import cazcade.vortex.pool.api.PoolObjectPresenterContainer;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenterFactory;
import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractContainerObjectPresenterImpl<T extends PoolObjectView> extends AbstractPoolObjectPresenter<T> implements PoolObjectDropTarget, PoolObjectPresenterContainer {
    private HashMap<LiquidURI, Widget> poolObjectWidgetsByURI = new HashMap<LiquidURI, Widget>();
    private HashMap<LiquidURI, PoolObjectPresenter> objectPresenters = new HashMap<LiquidURI, PoolObjectPresenter>();

    public AbstractContainerObjectPresenterImpl(PoolPresenter pool, LSDEntity entity, T widget, final VortexThreadSafeExecutor threadSafeExecutor, final FormatUtil features) {
        super(pool, entity, widget, threadSafeExecutor);
        BusFactory.getInstance().listenForURIAndRequestType(entity.getURI(), LiquidRequestType.CREATE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (message.getOrigin() == LiquidMessageOrigin.SERVER) {
                    try {
                        final LSDEntity requestEntity = message.getResponse();
                        ClientLog.log("Adding " + requestEntity.getTypeDef().asString());
                        PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(AbstractContainerObjectPresenterImpl.this, requestEntity, features, threadSafeExecutor);
                        if (poolObjectPresenter != null) {
                            add(poolObjectPresenter);
                        }

                    } catch (Throwable e) {
                        ClientLog.log(e);
                    }
                }

            }
        });
        BusFactory.getInstance().listenForURIAndRequestType(entity.getURI(), LiquidRequestType.DELETE_POOL_OBJECT, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                if (message.getOrigin() == LiquidMessageOrigin.SERVER) {
                    try {
                        final LSDEntity response = message.getResponse();
                        final LiquidURI uri = response.getURI();
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
    public void remove(PoolObjectPresenter presenter) {
        final LiquidURI uri = presenter.getEntity().getURI();
        objectPresenters.remove(uri);
        poolObjectWidgetsByURI.remove(uri);
        getPoolObjectView().removeView(presenter.getPoolObjectView());
    }


    @Override
    public boolean willAccept(LSDTypeDef type) {
        return true;
    }

    @Override
    public void accept(PoolObjectPresenter source) {
        getPool().transfer(source, this);
    }

    @Override
    public void add(PoolObjectPresenter presenter) {
        final LiquidURI uri = presenter.getEntity().getURI();
        objectPresenters.put(uri, presenter);
        poolObjectWidgetsByURI.put(uri, presenter.getPoolObjectView());
        getPoolObjectView().addView(presenter.getPoolObjectView());
    }
}
