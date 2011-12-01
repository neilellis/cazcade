package cazcade.vortex.pool.objects.youtube;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
    public YouTubePresenter(final PoolPresenter pool, final LSDTransferEntity entity, final YouTubeView widget, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        getPoolObjectView().addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                PoolObjectEditor.showForEdit(new YouTubeEditorPanel(getEntity()), null);

            }
        }, DoubleClickEvent.getType());

    }

    @Override
    public void update(@Nonnull final LSDTransferEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (newEntity.hasAttribute(LSDAttribute.EURI)) {
                    getPoolObjectView().setVideoId(new LiquidURI(newEntity.getAttribute(LSDAttribute.EURI)).getSubURI().toString());
                }
                YouTubePresenter.super.update(newEntity, replaceEntity);
                getPoolObjectView().resetMode();
            }
        });
    }

    @Override
    public void onHoldDrag(@Nonnull final HoldDragEvent dragEvent) {
        getPoolObjectView().imageViewOn();
        super.onHoldDrag(dragEvent);
    }

    @Override
    public void onDrag(@Nonnull final DragEvent dragEvent) {
        getPoolObjectView().imageViewOn();
        super.onDrag(dragEvent);
    }

    @Override
    public void onEndDrag(final EndDragEvent dragEvent) {
        super.onEndDrag(dragEvent);
        getPoolObjectView().imageViewOff();
    }

}
