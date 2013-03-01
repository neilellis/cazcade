/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.edit;

import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.Callback;
import cazcade.vortex.bus.client.Request;
import cazcade.vortex.common.client.events.*;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectEditorPanel extends EntityBackedFormPanel {

    protected void onSave() {

    }

    @Override public void save() {
        super.save();
        if (isValid()) {
            onSave();
            Request.updatePoolObject(getEntityDiff(), new Callback<UpdatePoolObjectRequest>() {
                @Override public void handle(UpdatePoolObjectRequest message) throws Exception {
                    $(message.response().$());
                    fireEvent(new EditFinishEvent());
                }
            });
        } else {
            Window.alert("Not valid.");
        }

    }

    @Override protected boolean isSaveOnExit() {
        return true;
    }

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
                    Request.updatePoolObject(field.getEntityDiff(), new Callback<UpdatePoolObjectRequest>() {
                                @Override public void handle(UpdatePoolObjectRequest message) throws Exception {
                                    $(message.response().$());
                                    if (autoCloseField(field)) { fireEvent(new EditFinishEvent()); }
                                }
                            }, new Callback<UpdatePoolObjectRequest>() {
                                @Override public void handle(UpdatePoolObjectRequest message) throws Exception {
                                    field.setErrorMessage(message.response().$(DESCRIPTION));
                                }
                            }
                                            );
                }
            }
        };
    }

    protected boolean autoCloseField(final Bindable field) {
        return false;
    }

    public HandlerRegistration addEditFinishHandler(@Nullable final EditFinishHandler onFinishAction) {
        return addHandler(onFinishAction, EditFinishEvent.TYPE);
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract String getCaption();

    public TransferEntity getEntityForCreation() {
        return $();
    }

    public void addValidHandler(ValidHandler validHandler) {
        addHandler(validHandler, ValidEvent.TYPE);
    }

    public void addInvalidHandler(InvalidHandler invalidHandler) {
        addHandler(invalidHandler, InvalidEvent.TYPE);
    }
}
