/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.custom;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.common.client.CustomObjectEditor;
import cazcade.vortex.common.client.events.EditStartEvent;
import cazcade.vortex.common.client.events.EditStartHandler;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class CustomObjectPresenter extends AbstractPoolObjectPresenter<CustomObjectView> {

    private final CustomObjectEditor customObjectEditor;

    public CustomObjectPresenter(final PoolPresenter pool, final TransferEntity entity, final CustomObjectView widget, final CustomObjectEditor customObjectEditor, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(pool, entity, widget, threadSafeExecutor);
        this.customObjectEditor = customObjectEditor;
    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                view().setImageUrl(newEntity.$(Dictionary.IMAGE_URL));
                final List<Entity> handlers = newEntity.children(Dictionary.EVENT_HANDLER);
                for (final Entity handler : handlers) {
                    registerHandlerWithView(view(), handler);
                }
                view().addHandler(new EditStartHandler() {
                    @Override
                    public void onEditStart(final EditStartEvent event) {
                        customObjectEditor.show(newEntity);
                    }
                }, EditStartEvent.TYPE);

                CustomObjectPresenter.super.update(newEntity, replaceEntity);

                customObjectEditor.setOnChangeAction(new CustomObjectEditor.ChangeAction() {
                    @Override
                    public void run(@Nonnull final TransferEntity updateEntity) {
                        Bus.get()
                           .send(new UpdatePoolObjectRequest(updateEntity), new AbstractMessageCallback<UpdatePoolObjectRequest>() {
                               @Override
                               public void onSuccess(final UpdatePoolObjectRequest original, @Nonnull final UpdatePoolObjectRequest message) {
                                   update(message.response().$(), true);
                               }
                           });

                    }
                });
            }
        });
    }

    private void registerHandlerWithView(@Nonnull final CustomObjectView poolObjectView, @Nonnull final Entity handler) {
        if (handler.canBe(Types.T_ACTIVATE_EVENT_HANDLER)) {
            if (entity.has(Dictionary.NAVIGATION_URL)) {
                poolObjectView.setHref(handler.$(Dictionary.NAVIGATION_URL));
            }
        }
    }

}
