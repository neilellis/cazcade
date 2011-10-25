package cazcade.vortex.pool.objects.microblog;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

/**
 * @author neilellis@cazcade.com
 */
public class MicroBlogPresenter extends AbstractPoolObjectPresenter<MicroBlogView> {
    public MicroBlogPresenter(PoolPresenter poolPresenter, LSDEntity entity, MicroBlogView microBlogView, VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, microBlogView, threadSafeExecutor);
    }

    @Override
    public void update(LSDEntity newEntity, boolean replaceEntity) {
        super.update(newEntity, replaceEntity);
        getPoolObjectView().setProfileImage(newEntity.getAttribute(LSDAttribute.ICON_URL));
        getPoolObjectView().setMicroBlogShortText(newEntity.getAttribute(LSDAttribute.TEXT_BRIEF));
        getPoolObjectView().setMicroBlogTitle(newEntity.getAttribute(LSDAttribute.TITLE));
    }

}
