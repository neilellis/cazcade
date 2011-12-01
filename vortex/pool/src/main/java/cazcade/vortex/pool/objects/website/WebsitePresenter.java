package cazcade.vortex.pool.objects.website;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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
    public WebsitePresenter(PoolPresenter pool, LSDEntity entity, final WebsiteView widget, VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        getPoolObjectView().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new WebsiteEditorPanel(getEntity()), null);

            }
        }, DoubleClickEvent.getType());

    }

    @Override
    public void update(@Nonnull final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setUrl(newEntity.getAttribute(LSDAttribute.SOURCE));
                WebsitePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
