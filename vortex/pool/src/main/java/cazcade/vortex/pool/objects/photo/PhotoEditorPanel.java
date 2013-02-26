/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * @author neilellis@cazcade.com
 */
public class PhotoEditorPanel extends AbstractPoolObjectEditorPanel implements PopupEditPanel {


    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, PhotoEditorPanel> {}

    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);
    @UiField ChangeImageUrlPanel changeImagePanel;
    @UiField VortexTextArea      description;
    @UiField RegexTextBox        title;


    public PhotoEditorPanel(final TransferEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        changeImagePanel.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        fireEvent(new EditFinishEvent());
                    }
                });
            }
        });
        bind(changeImagePanel, Dictionary.IMAGE_URL);
        bind(description, Dictionary.DESCRIPTION);
        bind(title, Dictionary.TITLE);
        $(entity);
    }


    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override @SuppressWarnings({"ObjectEquality"})
    protected boolean autoCloseField(final Bindable field) {
        return field == changeImagePanel;
    }

    @Override
    public int getHeight() {
        return 700;
    }

    @Override
    public int getWidth() {
        return 840;
    }

    @Override public String getCaption() {
        return "Choose Photo";
    }


}