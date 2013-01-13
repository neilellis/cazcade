/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.edit;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectEditorPanel extends EntityBackedFormPanel {

    @Nullable protected Runnable onFinishAction;
    private             boolean  create;


    @Nonnull @Override
    protected String getReferenceDataPrefix() {
        return "object";
    }


    @Nonnull @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                if (field.isValid() && field.isBound()) {
                    getBus().send(new UpdatePoolObjectRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                        @Override
                        public void onSuccess(final UpdatePoolObjectRequest message, @Nonnull final UpdatePoolObjectRequest response) {
                            setEntity(response.getResponse().copy());
                            if (autoCloseField(field)) {
                                if (onFinishAction != null) {
                                    onFinishAction.run();
                                }
                            }
                        }

                        @Override
                        public void onFailure(final UpdatePoolObjectRequest message, @Nonnull final UpdatePoolObjectRequest response) {
                            field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                        }
                    });
                }
                else {

                }
            }
        };
    }

    protected boolean autoCloseField(final Bindable field) {
        return false;
    }

    public void setOnFinishAction(@Nullable final Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }


    public abstract int getHeight();

    public abstract int getWidth();


    public void setCreate(final boolean create) {
        this.create = create;
    }


}
