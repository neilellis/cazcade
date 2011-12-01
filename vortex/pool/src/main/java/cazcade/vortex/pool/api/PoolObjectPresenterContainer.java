package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.pool.objects.PoolObjectPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectPresenterContainer {

    void add(PoolObjectPresenter presenter);

    void remove(PoolObjectPresenter presenter);

    @Nonnull
    LSDDictionaryTypes getType();

    LSDTransferEntity getEntity();
}
