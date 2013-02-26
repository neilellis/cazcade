/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.events.*;
import cazcade.vortex.widgets.client.popup.VortexDialogPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class EditableImage extends Composite implements Bindable, HasValueChangeHandlers<String> {
    interface EditableImageUiBinder extends UiBinder<HTMLPanel, EditableImage> {}

    private static final EditableImageUiBinder ourUiBinder = GWT.create(EditableImageUiBinder.class);
    protected TransferEntity entity;
    protected boolean editable = true;
    @UiField CachedImage image;
    @UiField SpanElement editText;
    private  Attribute   attribute;

    public EditableImage() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        setEditable(true);

        //        image.setOnChangeAction(new Runnable() {
        //            @Override public void run() {
        //                ValueChangeEvent.fire(EditableImage.this, image.getUrl());
        //            }
        //        });
    }

    @Override
    public void bind(@Nonnull final TransferEntity entity, final Attribute attribute, final String referenceDataPrefix) {
        this.entity = entity;
        this.attribute = attribute;
        if (entity.has$(attribute)) {
            String url = entity.$(attribute);
            if (!url.equals(image.getUrl())) {
                image.setUrl(url);
            }
        }
        updateEditText();

    }

    @Override
    public HandlerRegistration addChangeHandler(final ValueChangeHandler onChangeAction) {
        return addValueChangeHandler(onChangeAction);
    }

    @Override public void clear() {
        image.clear();
        this.entity = SimpleEntity.emptyUnmodifiable();
    }

    @Override
    public void setErrorMessage(final String message) {
        Window.alert(message);
    }

    @Nonnull @Override
    public TransferEntity getEntityDiff() {
        return (TransferEntity) entity.asUpdateEntity().$(attribute, image.getRawUrl());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override public boolean isBound() {
        return attribute != null;
    }

    @Override public boolean isMultiValue() {
        return false;
    }

    @Override public Attribute getBoundAttribute() {
        return attribute;
    }

    @Override public List getStringValues() {
        throw new UnsupportedOperationException("Single value field");
    }

    @Override public String getStringValue() {
        return image.getRawUrl();
    }

    @Override public HandlerRegistration addInvalidHandler(InvalidHandler invalidHandler) {
        return addHandler(invalidHandler, InvalidEvent.TYPE);
    }

    @Override public HandlerRegistration addValidHandler(ValidHandler validHandler) {
        return addHandler(validHandler, ValidEvent.TYPE);
    }

    @UiHandler("image")
    public void onClick(final ClickEvent e) {
        if (entity.default$bool(Dictionary.EDITABLE, false) && editable) {
            final ImageEditorDialogBox imageEditorDialogBox = new ImageEditorDialogBox();
            imageEditorDialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(final CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            ValueChangeEvent.fire(EditableImage.this, image.getUrl());
                        }
                    });
                }
            });
            imageEditorDialogBox.showDown();


        }
    }

    public void setSize(final String size) {
        image.setSize(size);
    }

    @Override
    public void setHeight(@Nonnull final String height) {
        super.setHeight(height);
        image.setHeight(height);
    }

    @Override
    public void setWidth(@Nonnull final String width) {
        image.setWidth(width);
        super.setWidth(width);
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
        updateEditText();
    }

    private void updateEditText() {
        editText.getStyle()
                .setVisibility((this.editable && (entity == null || entity.default$bool(Dictionary.EDITABLE, true)))
                               ? Style.Visibility.VISIBLE
                               : Style.Visibility.HIDDEN);
    }

    @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private class ImageEditorDialogBox extends VortexDialogPanel {
        private ImageEditorDialogBox() {
            super();
            final ImageEditor editor = new ImageEditor(image);
            setMainPanel(editor);
            setWidth("840px");
            setHeight("610px");
            setText("Edit Image");
            addEditFinishHandler(new EditFinishHandler() {
                @Override public void onEditFinish(EditFinishEvent event) {
                    hide();
                }
            });
            addEditCancelHandler(new EditCancelHandler() {
                @Override public void onEditCancel(EditCancelEvent event) {
                    hide();
                }
            });
        }

    }

}