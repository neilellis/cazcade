/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.richtext;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RichTextPresenter extends AbstractPoolObjectPresenter<RichTextView> {
    public RichTextPresenter(final PoolPresenter poolPresenter, final TransferEntity entity, @Nonnull final RichTextView view, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, view, threadSafeExecutor);
        //make sure we set editable before setText (very important)
        view.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                final TransferEntity minimalEntity = entity().asUpdate();
                minimalEntity.$(Dictionary.TEXT_EXTENDED, view.getText());
                Bus.get().send(new UpdatePoolObjectRequest(minimalEntity), new AbstractMessageCallback<UpdatePoolObjectRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolObjectRequest original, final UpdatePoolObjectRequest message) {
                    }
                });
            }
        });
    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                RichTextPresenter.super.update(newEntity, replaceEntity);
                if (newEntity.has(Dictionary.TEXT_EXTENDED)) {
                    view().setText(newEntity.$(Dictionary.TEXT_EXTENDED));
                } else {
                    view().setText("");
                }
            }
        });
    }

    @Override
    protected int getDefaultWidth() {
        return 200;
    }
}