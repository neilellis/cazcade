package cazcade.vortex.pool.objects.custom;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.common.client.CustomObjectEditor;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.EditStart;
import cazcade.vortex.pool.EditStartHandler;
import cazcade.vortex.pool.api.PoolPresenter;

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class CustomObjectPresenter extends AbstractPoolObjectPresenter<CustomObjectView> {

    private CustomObjectEditor customObjectEditor;

    public CustomObjectPresenter(PoolPresenter pool, LSDEntity entity, final CustomObjectView widget, CustomObjectEditor customObjectEditor, VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        this.customObjectEditor = customObjectEditor;
    }

    @Override
    public void update(final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getPoolObjectView().setImageUrl(newEntity.getAttribute(LSDAttribute.IMAGE_URL));
                List<LSDEntity> handlers = newEntity.getSubEntities(LSDAttribute.EVENT_HANDLER);
                for (LSDEntity handler : handlers) {
                    registerHandlerWithView(getPoolObjectView(), handler);
                }
                getPoolObjectView().addHandler(new EditStartHandler() {
                    @Override
                    public void onEditStart(EditStart event) {
                        customObjectEditor.show(newEntity);
                    }
                }, EditStart.TYPE);

                CustomObjectPresenter.super.update(newEntity, replaceEntity);

                customObjectEditor.setOnChangeAction(new CustomObjectEditor.ChangeAction() {
                    @Override
                    public void run(LSDEntity updateEntity) {
                        bus.send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                            @Override
                            public void onSuccess(UpdatePoolObjectRequest message, UpdatePoolObjectRequest response) {
                                update(response.getResponse().copy(), true);
                            }
                        });

                    }
                });
            }
        });
    }

    private void registerHandlerWithView(CustomObjectView poolObjectView, LSDEntity handler) {
        if (handler.canBe(LSDDictionaryTypes.ACTIVATE_EVENT_HANDLER)) {
            if (entity.hasAttribute(LSDAttribute.NAVIGATION_URL)) {
                poolObjectView.setHref(handler.getAttribute(LSDAttribute.NAVIGATION_URL));
            }
        }
    }

}
