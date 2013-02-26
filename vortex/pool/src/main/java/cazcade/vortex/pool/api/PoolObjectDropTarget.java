/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.TypeDef;
import cazcade.vortex.pool.objects.PoolObjectPresenter;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectDropTarget {

    boolean willAccept(TypeDef type);

    void accept(PoolObjectPresenter source);

}
