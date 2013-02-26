/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.microblog;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class MicroBlogPresenter extends AbstractPoolObjectPresenter<MicroBlogView> {
    public MicroBlogPresenter(final PoolPresenter poolPresenter, final TransferEntity entity, final MicroBlogView microBlogView, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, microBlogView, threadSafeExecutor);
    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        super.update(newEntity, replaceEntity);
        getPoolObjectView().setProfileImage(newEntity.$(Dictionary.ICON_URL));
        getPoolObjectView().setMicroBlogShortText(newEntity.$(Dictionary.TEXT_BRIEF));
        getPoolObjectView().setMicroBlogTitle(newEntity.$(Dictionary.TITLE));
    }

}
