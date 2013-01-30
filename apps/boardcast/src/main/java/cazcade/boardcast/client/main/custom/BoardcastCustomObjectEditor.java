/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.custom;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.CustomObjectEditor;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import cazcade.vortex.widgets.client.image.ImageUploader;
import cazcade.vortex.widgets.client.image.OnFinishUploadHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class BoardcastCustomObjectEditor extends Composite implements CustomObjectEditor {
    private static final EditorUiBinder ourUiBinder = GWT.create(EditorUiBinder.class);

    @UiField PopupPanel popup;

    @UiField ImageUploader imageUploader;

    @UiField Label changeButton;

    @UiField Label          cancelButton;
    @UiField RegexTextBox   widthField;
    @UiField RegexTextBox   heightField;
    @UiField VortexTextArea scriptField;

    private LSDTransferEntity updateEntity;
    private boolean           sizeDirty;

    private ChangeAction onChangeAction;

    public BoardcastCustomObjectEditor() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        imageUploader.setOnFinishHandler(new OnFinishUploadHandler() {
            @Override
            public void onFinish(@Nonnull final ImageUploader uploader) {
                if (uploader.getStatus() == ImageUploader.Status.SUCCESS) {
                    final String url = uploader.getImageUrl();
                    updateEntity.setAttribute(LSDAttribute.IMAGE_URL, url);
                    updateEntity.setAttribute(LSDAttribute.ICON_URL, url);
                    imageUploader.setImageUrl(url);
                }
                else {
                    Window.alert("Failed to upload image.");
                }
            }
        });


        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (sizeDirty) {
                    BusFactory.getInstance()
                              .send(new ResizePoolObjectRequest(updateEntity.getURI(), Integer.parseInt(widthField.getValue()) * 40,
                                      Integer.parseInt(heightField.getValue())
                                      * 40), new AbstractResponseCallback<ResizePoolObjectRequest>() {
                                  @Override
                                  public void onSuccess(final ResizePoolObjectRequest message, final ResizePoolObjectRequest response) {
                                  }
                              });
                }
                BusFactory.getInstance()
                          .send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                              @Override
                              public void onSuccess(final UpdatePoolObjectRequest message, final UpdatePoolObjectRequest response) {
                                  sizeDirty = false;
                                  try {
                                      popup.hide();
                                  } catch (Exception e) {
                                      ClientLog.log("Mysterious popup.hide() exception." + e.getMessage());
                                  }
                                  onChangeAction.run(updateEntity);
                              }
                          });
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                try {
                    popup.hide();
                } catch (Exception e) {
                    ClientLog.log("Mysterious popup.hide() exception." + e.getMessage());
                }
            }
        });

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        }, ClickEvent.getType());
    }

    @Override
    public void show(@Nonnull final LSDTransferEntity object) {
        updateEntity = object.asUpdateEntity();
        imageUploader.setImageUrl(object.getAttribute(LSDAttribute.IMAGE_URL));
        final LSDBaseEntity view = object.getSubEntity(LSDAttribute.VIEW, false);
        widthField.setValue(view.getAttribute(LSDAttribute.VIEW_WIDTH));
        heightField.setValue(view.getAttribute(LSDAttribute.VIEW_HEIGHT));
        final Runnable sizeDirtyAction = new Runnable() {
            @Override
            public void run() {
                sizeDirty = true;
            }
        };
        widthField.setOnChangeAction(sizeDirtyAction);
        heightField.setOnChangeAction(sizeDirtyAction);
        scriptField.bind(updateEntity, LSDAttribute.SERVER_SCRIPT, "");

        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(final int offsetWidth, final int offsetHeight) {
                popup.setPopupPosition(Window.getClientWidth() / 2 - offsetWidth / 2, Window.getClientHeight() / 2
                                                                                      - offsetHeight / 2);
            }
        });
    }

    @Override
    public void setOnChangeAction(final ChangeAction onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    interface EditorUiBinder extends UiBinder<HTMLPanel, BoardcastCustomObjectEditor> {}
}