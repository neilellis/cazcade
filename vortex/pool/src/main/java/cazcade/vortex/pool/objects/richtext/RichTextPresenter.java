/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.richtext;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
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
                final TransferEntity minimalEntity = getEntity().asUpdateEntity();
                minimalEntity.$(Dictionary.TEXT_EXTENDED, view.getText());
                bus.send(new UpdatePoolObjectRequest(minimalEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolObjectRequest message, final UpdatePoolObjectRequest response) {
                    }
                });
            }
        });
    }

    @Override
    public void update(@Nonnull final TransferEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                RichTextPresenter.super.update(newEntity, replaceEntity);
                if (newEntity.has$(Dictionary.TEXT_EXTENDED)) {
                    getPoolObjectView().setText(newEntity.$(Dictionary.TEXT_EXTENDED));
                } else {
                    getPoolObjectView().setText("");
                }
            }
        });
    }

    @Override
    protected int getDefaultWidth() {
        return 200;
    }
}