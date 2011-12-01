package cazcade.vortex.pool.objects.website;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.edit.PoolObjectEditor;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class WebsitePresenter extends AbstractPoolObjectPresenter<WebsiteView> {
    public WebsitePresenter(final PoolPresenter pool, final LSDTransferEntity entity, final WebsiteView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        getPoolObjectView().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new WebsiteEditorPanel(getEntity()), null);

            }
        }, DoubleClickEvent.getType());

    }

    @Override
    public void update(@Nonnull final LSDTransferEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setUrl(newEntity.getAttribute(LSDAttribute.SOURCE));
                WebsitePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
