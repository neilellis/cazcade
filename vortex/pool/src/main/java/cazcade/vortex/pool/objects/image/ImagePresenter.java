package cazcade.vortex.pool.objects.image;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ImagePresenter extends AbstractPoolObjectPresenter<ImageView> {
    public ImagePresenter(final PoolPresenter pool, final LSDEntity entity, final ImageView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);

    }

    @Override
    public void update(@Nonnull final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setUrl(newEntity.getAttribute(LSDAttribute.IMAGE_URL));
                ImagePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
