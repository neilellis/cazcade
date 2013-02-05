/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class ImageEditor extends Composite implements PopupEditPanel {
    interface ImageEditorUiBinder extends UiBinder<HTMLPanel, ImageEditor> {}

    private static final ImageEditorUiBinder ourUiBinder = GWT.create(ImageEditorUiBinder.class);
    @UiField ChangeImageUrlPanel changeImagePanel;

    public ImageEditor(@Nonnull final CachedImage displayImage) {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        changeImagePanel.setValue(displayImage.getRawUrl());
        changeImagePanel.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                displayImage.setUrl(changeImagePanel.getStringValue());
            }
        });
    }

    @Nullable
    public String getUrl() {
        return changeImagePanel.getStringValue();
    }

    @Override
    public boolean isValid() {
        return changeImagePanel.isValid();
    }
}