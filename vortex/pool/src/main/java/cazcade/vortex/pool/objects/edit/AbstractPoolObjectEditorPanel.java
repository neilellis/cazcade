/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.edit;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.user.client.Window;

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

    @Override protected boolean isSaveOnExit() {
        return true;
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

    protected void onSave() {

    }


    @Override public void save() {
        super.save();
        if (isValid()) {
            onSave();
            getBus().send(new UpdatePoolObjectRequest(getEntityDiff()), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                @Override
                public void onSuccess(final UpdatePoolObjectRequest message, @Nonnull final UpdatePoolObjectRequest response) {
                    setEntity(response.getResponse().copy());
                }

                @Override
                public void onFailure(final UpdatePoolObjectRequest message, @Nonnull final UpdatePoolObjectRequest response) {
                    Window.alert(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                }
            });
        }
        else {
            Window.alert("Not valid.");
        }

    }

    protected boolean autoCloseField(final Bindable field) {
        return false;
    }

    public void setOnFinishAction(@Nullable final Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }


    public abstract int getHeight();

    public abstract int getWidth();

    public abstract String getCaption();


    public void setCreate(final boolean create) {
        this.create = create;
    }


    public LSDTransferEntity getEntityForCreation() {
        return getEntity();
    }
}
