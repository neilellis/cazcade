/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.edit.PoolObjectEditor;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public class PhotoPresenter extends AbstractPoolObjectPresenter<PhotoView> {
    public PhotoPresenter(final PoolPresenter pool, final TransferEntity entity, final PhotoView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        view().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new PhotoEditorPanel(entity()), null);

            }
        });

    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (newEntity.has(IMAGE_URL)) {
                    view().setUrl(newEntity.$(IMAGE_URL));
                }
                PhotoPresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
