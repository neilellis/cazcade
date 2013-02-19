/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.website;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.*;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.UrlField;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class WebsiteEditorPanel extends AbstractPoolObjectEditorPanel {

    interface WebsiteEditorUIBinder extends UiBinder<HTMLPanel, WebsiteEditorPanel> {}

    private static final WebsiteEditorUIBinder ourUiBinder = GWT.create(WebsiteEditorUIBinder.class);
    @UiField Button         done;
    @UiField VortexTextArea description;
    @UiField RegexTextBox   title;
    @UiField UrlField       urlField;

    public WebsiteEditorPanel(final LSDTransferEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));

        urlField.addValidHandler(new ValidHandler() {
            @Override public void onValid(ValidEvent event) {
                fireEvent(event);
            }
        });
        urlField.addInvalidHandler(new InvalidHandler() {
            @Override public void onInvalid(InvalidEvent event) {
                fireEvent(event);
            }
        });
        addBinding(urlField, LSDAttribute.SOURCE);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(title, LSDAttribute.TITLE);
        setEntity(entity);
    }

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        fireEvent(new EditFinishEvent());
    }

    @Override
    public void bindEntity(final LSDTransferEntity entity) {
        super.bindEntity(entity);
    }

    @Override
    public int getHeight() {
        return 360;
    }

    @Override
    public int getWidth() {
        return 700;
    }

    @Override public String getCaption() {
        return "Choose website";
    }
}