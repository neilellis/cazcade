package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTypeDef;
import cazcade.vortex.pool.objects.PoolObjectPresenter;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectDropTarget {

    boolean willAccept(LSDTypeDef type);
    void accept(PoolObjectPresenter source);

}
