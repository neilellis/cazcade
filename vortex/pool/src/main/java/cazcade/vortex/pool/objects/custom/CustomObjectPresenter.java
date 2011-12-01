package cazcade.vortex.pool.objects.custom;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.common.client.CustomObjectEditor;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.EditStart;
import cazcade.vortex.pool.EditStartHandler;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class CustomObjectPresenter extends AbstractPoolObjectPresenter<CustomObjectView> {

    private final CustomObjectEditor customObjectEditor;

    public CustomObjectPresenter(final PoolPresenter pool, final LSDTransferEntity entity, final CustomObjectView widget, final CustomObjectEditor customObjectEditor, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        this.customObjectEditor = customObjectEditor;
    }

    @Override
    public void update(@Nonnull final LSDTransferEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setImageUrl(newEntity.getAttribute(LSDAttribute.IMAGE_URL));
                final List<LSDBaseEntity> handlers = newEntity.getSubEntities(LSDAttribute.EVENT_HANDLER);
                for (final LSDBaseEntity handler : handlers) {
                    registerHandlerWithView(getPoolObjectView(), handler);
                }
                getPoolObjectView().addHandler(new EditStartHandler() {
                    @Override
                    public void onEditStart(final EditStart event) {
                        customObjectEditor.show(newEntity);
                    }
                }, EditStart.TYPE);

                CustomObjectPresenter.super.update(newEntity, replaceEntity);

                customObjectEditor.setOnChangeAction(new CustomObjectEditor.ChangeAction() {
                    @Override
                    public void run(@Nonnull final LSDTransferEntity updateEntity) {
                        bus.send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                            @Override
                            public void onSuccess(final UpdatePoolObjectRequest message, @Nonnull final UpdatePoolObjectRequest response) {
                                update(response.getResponse().copy(), true);
                            }
                        });

                    }
                });
            }
        });
    }

    private void registerHandlerWithView(@Nonnull final CustomObjectView poolObjectView, @Nonnull final LSDBaseEntity handler) {
        if (handler.canBe(LSDDictionaryTypes.ACTIVATE_EVENT_HANDLER)) {
            if (entity.hasAttribute(LSDAttribute.NAVIGATION_URL)) {
                poolObjectView.setHref(handler.getAttribute(LSDAttribute.NAVIGATION_URL));
            }
        }
    }

}
