package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.edit.PoolObjectEditor;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

/**
 * @author neilellis@cazcade.com
 */
public class PhotoPresenter extends AbstractPoolObjectPresenter<PhotoView> {
    public PhotoPresenter(PoolPresenter pool, LSDEntity entity, final PhotoView widget, VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        getPoolObjectView().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new PhotoEditorPanel(getEntity()), null);

            }
        });

    }

    @Override
    public void update(final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setUrl(newEntity.getAttribute(LSDAttribute.IMAGE_URL));
                PhotoPresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
