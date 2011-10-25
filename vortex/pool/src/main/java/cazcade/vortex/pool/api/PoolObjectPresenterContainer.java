package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.objects.PoolObjectPresenter;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectPresenterContainer {

    void add(PoolObjectPresenter presenter);

    void remove(PoolObjectPresenter presenter);

    LSDDictionaryTypes getType();

    LSDEntity getEntity();
}
