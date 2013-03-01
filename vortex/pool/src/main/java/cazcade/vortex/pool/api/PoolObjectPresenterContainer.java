/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.pool.objects.PoolObjectPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectPresenterContainer {

    void add(PoolObjectPresenter presenter);

    void remove(PoolObjectPresenter presenter);

    @Nonnull Types getType();

    TransferEntity entity();
}
