package cazcade.vortex.pool.objects.alias;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AliasReferencePresenter extends AbstractPoolObjectPresenter<AliasReferenceView> {
    public AliasReferencePresenter(PoolPresenter pool, LSDEntity entity, final AliasReferenceView widget, VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
    }

    @Override
    public void update(@Nonnull final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setAliasURI(newEntity.getAttributeAsURI(LSDAttribute.SOURCE));
                AliasReferencePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }

}
