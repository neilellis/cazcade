package cazcade.vortex.pool.objects.microblog;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class MicroBlogPresenter extends AbstractPoolObjectPresenter<MicroBlogView> {
    public MicroBlogPresenter(final PoolPresenter poolPresenter, final LSDEntity entity, final MicroBlogView microBlogView, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, microBlogView, threadSafeExecutor);
    }

    @Override
    public void update(@Nonnull final LSDEntity newEntity, final boolean replaceEntity) {
        super.update(newEntity, replaceEntity);
        getPoolObjectView().setProfileImage(newEntity.getAttribute(LSDAttribute.ICON_URL));
        getPoolObjectView().setMicroBlogShortText(newEntity.getAttribute(LSDAttribute.TEXT_BRIEF));
        getPoolObjectView().setMicroBlogTitle(newEntity.getAttribute(LSDAttribute.TITLE));
    }

}
