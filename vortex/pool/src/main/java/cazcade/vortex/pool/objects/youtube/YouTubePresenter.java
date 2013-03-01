/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.youtube;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.dnd.client.gesture.drag.DragEvent;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragEvent;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragEvent;
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
public class YouTubePresenter extends AbstractPoolObjectPresenter<YouTubeView> {
    public YouTubePresenter(final PoolPresenter pool, final TransferEntity entity, final YouTubeView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        view().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new YouTubeEditorPanel(entity()), null);

            }
        }, DoubleClickEvent.getType());

    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (newEntity.has(Dictionary.MEDIA_ID)) {
                    view().setVideoId(newEntity.$(Dictionary.MEDIA_ID));
                }
                YouTubePresenter.super.update(newEntity, replaceEntity);
                view().resetMode();
            }
        });
    }

    @Override
    public void onHoldDrag(@Nonnull final HoldDragEvent dragEvent) {
        view().imageViewOn();
        super.onHoldDrag(dragEvent);
    }

    @Override
    public void onDrag(@Nonnull final DragEvent dragEvent) {
        view().imageViewOn();
        super.onDrag(dragEvent);
    }

    @Override
    public void onEndDrag(final EndDragEvent dragEvent) {
        super.onEndDrag(dragEvent);
        view().imageViewOff();
    }

}
