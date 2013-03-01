/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects;

import cazcade.liquid.api.lsd.TransferEntity;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectPresenter<T extends PoolObjectView> {

    TransferEntity entity();

    Widget view();

    void select();

    /**
     * The pool presenter calls this method when the object is added to pool.
     *
     * @param count
     */
    void onAddToPool(int count);

    void onRemoveFromPool();

    void setOnDelete(Runnable runnable);

    double getX();

    double getY();

    double getLeft();

    double getRight();

    double getTop();

    double getBottom();

    void hide();

    void setX(double x);

    void setY(double y);
}
