/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.account;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditFinishHandler;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.image.ImageUploader;
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
public class ProfileEditor extends Composite {
    private static final ProfileImageUiBinder ourUiBinder = GWT.create(ProfileImageUiBinder.class);

    @UiField PopupPanel popup;

    @UiField ImageUploader imageUploader;
    @UiField Label         changeButton;
    @UiField Label         cancelButton;

    private ChangeAction onChangeAction;

    public ProfileEditor(@Nonnull final LSDTransferEntity alias) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        final LSDTransferEntity updateEntity = alias.asUpdateEntity();
        imageUploader.setImageUrl(alias.getAttribute(LSDAttribute.IMAGE_URL));
        imageUploader.setOnFinishHandler(new EditFinishHandler() {
            @Override public void onEditFinish(EditFinishEvent event) {
                if (imageUploader.getStatus() == ImageUploader.Status.SUCCESS) {
                    final String url = imageUploader.getImageUrl();
                    updateEntity.setAttribute(LSDAttribute.IMAGE_URL, url);
                    updateEntity.setAttribute(LSDAttribute.ICON_URL, url);
                    imageUploader.setImageUrl(url);
                    // The server sends useful information to the client by default
                }
                else {
                    Window.alert("Failed to upload image.");
                }
            }
        });


        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                BusFactory.getInstance()
                          .send(new UpdateAliasRequest(LiquidSessionIdentifier.ANON, updateEntity), new AbstractResponseCallback<UpdateAliasRequest>() {
                              @Override
                              public void onSuccess(final UpdateAliasRequest message, final UpdateAliasRequest response) {
                                  try {
                                      popup.hide();
                                  } catch (Exception e) {
                                      ClientLog.log(e);
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
                    ClientLog.log(e);
                }
            }
        });
    }

    public void show() {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(final int offsetWidth, final int offsetHeight) {
                popup.setPopupPosition(Window.getClientWidth() / 2 - offsetWidth / 2, Window.getClientHeight() / 2
                                                                                      - offsetHeight / 2);
            }
        });
    }

    public void setOnChangeAction(final ChangeAction onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    interface ProfileImageUiBinder extends UiBinder<HTMLPanel, ProfileEditor> {}

    public interface ChangeAction {
        void run(LSDBaseEntity newAlias);
    }
}