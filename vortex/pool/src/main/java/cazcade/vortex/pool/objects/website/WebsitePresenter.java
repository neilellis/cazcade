/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.website;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.gwt.util.client.ClientLog;
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
    public WebsitePresenter(final PoolPresenter pool, final TransferEntity entity, final WebsiteView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        view().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new WebsiteEditorPanel(entity()), null);

            }
        }, DoubleClickEvent.getType());

    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (newEntity.has(Dictionary.SOURCE)) {
                    view().setUrl(newEntity.$(Dictionary.SOURCE));
                } else {
                    ClientLog.warn("No source for " + newEntity.uri());
                }
                WebsitePresenter.super.update(newEntity, replaceEntity);
            }
        });
    }


}
