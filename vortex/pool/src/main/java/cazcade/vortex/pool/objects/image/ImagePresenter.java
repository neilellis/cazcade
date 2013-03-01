/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.image;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ImagePresenter extends AbstractPoolObjectPresenter<ImageView> {
    public ImagePresenter(final PoolPresenter pool, final TransferEntity entity, final ImageView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);

    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                view().setUrl(newEntity.$(Dictionary.IMAGE_URL));
                ImagePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
