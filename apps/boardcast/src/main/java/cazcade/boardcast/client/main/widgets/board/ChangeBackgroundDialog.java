/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.*;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.image.ImageOption;
import cazcade.vortex.widgets.client.image.ImageSelection;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import cazcade.vortex.widgets.client.popup.VortexDialogPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeBackgroundDialog extends Composite implements Bindable, PopupEditPanel {
    private static final ChangeBackgroundDialogUiBinder ourUiBinder = GWT.create(ChangeBackgroundDialogUiBinder.class);
    @UiField ChangeImageUrlPanel changeBackgroundPanel;
    @UiField ImageSelection      imageSelector;
    private  LSDBaseEntity       oldEntity;
    private  VortexDialogPanel   vortexDialogPanel;


    public ChangeBackgroundDialog() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        imageSelector.setSelectionAction(new ImageSelection.SelectionAction() {
            @Override
            public void onSelect(@Nonnull final ImageOption imageOption) {
                changeBackgroundPanel.setValue(imageOption.getUrl());
                changeBackgroundPanel.processChange();
            }
        });
        vortexDialogPanel = new VortexDialogPanel();
        changeBackgroundPanel.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                vortexDialogPanel.hide();
            }
        });

    }

    @Override public HandlerRegistration addChangeHandler(ValueChangeHandler onChangeAction) {
        return changeBackgroundPanel.addChangeHandler(onChangeAction);
    }

    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String referenceDataPrefix) {
        oldEntity = entity.copy();
        changeBackgroundPanel.bind(entity, attribute, referenceDataPrefix);
    }

    @Override
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity updateEntity = changeBackgroundPanel.getEntityDiff();
        //        final String oldIconUrl = oldEntity.getAttribute(LSDAttribute.ICON_URL);
        //        if (oldIconUrl == null || oldIconUrl.equals(oldEntity.getAttribute(LSDAttribute.IMAGE_URL))) {
        //            updateEntity.setAttribute(LSDAttribute.ICON_URL, changeBackgroundPanel.getStringValue());
        //        }
        return updateEntity;
    }

    @Override
    public boolean isValid() {
        return changeBackgroundPanel.isValid();
    }

    @Override public boolean isBound() {
        return changeBackgroundPanel.isBound();
    }

    @Override public boolean isMultiValue() {
        return false;
    }

    @Override public LSDAttribute getBoundAttribute() {
        return changeBackgroundPanel.getBoundAttribute();
    }

    @Override public List getStringValues() {
        throw new UnsupportedOperationException("Single value field");
    }

    @Override public String getStringValue() {
        return changeBackgroundPanel.getStringValue();
    }

    @Override public HandlerRegistration addInvalidHandler(InvalidHandler invalidHandler) {
        return changeBackgroundPanel.addInvalidHandler(invalidHandler);
    }

    @Override public HandlerRegistration addValidHandler(ValidHandler validHandler) {
        return changeBackgroundPanel.addValidHandler(validHandler);
    }

    @Override
    public void setErrorMessage(final String message) {
        Window.alert(message);
    }


    public void show() {
        vortexDialogPanel.setMainPanel(this);
        vortexDialogPanel.setAutoHideEnabled(true);
        vortexDialogPanel.setAutoHideOnHistoryEventsEnabled(true);
        vortexDialogPanel.setWidth("820px");
        vortexDialogPanel.setHeight("640px");
        vortexDialogPanel.showDown();
        vortexDialogPanel.setText("Change Background");
        vortexDialogPanel.addEditFinishHandler(new EditFinishHandler() {
            @Override public void onEditFinish(EditFinishEvent event) {
                vortexDialogPanel.hide();
            }
        });
        vortexDialogPanel.addEditCancelHandler(new EditCancelHandler() {
            @Override public void onEditCancel(EditCancelEvent event) {
                vortexDialogPanel.hide();
            }
        });
    }

    interface ChangeBackgroundDialogUiBinder extends UiBinder<HTMLPanel, ChangeBackgroundDialog> {}
}