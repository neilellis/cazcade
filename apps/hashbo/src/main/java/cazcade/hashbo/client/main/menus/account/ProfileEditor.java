package cazcade.hashbo.client.main.menus.account;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.image.ImageUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;

/**
 * @author neilellis@cazcade.com
 */
public class ProfileEditor extends Composite {

    public void show() {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                popup.setPopupPosition(Window.getClientWidth() / 2 - offsetWidth / 2, Window.getClientHeight() / 2 - offsetHeight / 2);
            }
        });
    }

    public void setOnChangeAction(ChangeAction onChangeAction) {
        this.onChangeAction = onChangeAction;
    }


    interface ProfileImageUiBinder extends UiBinder<HTMLPanel, ProfileEditor> {
    }

    private static ProfileImageUiBinder ourUiBinder = GWT.create(ProfileImageUiBinder.class);

    private ChangeAction onChangeAction;

    @UiField
    PopupPanel popup;

    @UiField
    ImageUploader imageUploader;
    @UiField
    Label changeButton;
    @UiField
    Label cancelButton;

    public ProfileEditor(final LSDEntity alias) {
        initWidget(ourUiBinder.createAndBindUi(this));
        final LSDEntity updateEntity = alias.asUpdateEntity();
        imageUploader.setImageURL(alias.getAttribute(LSDAttribute.IMAGE_URL));
        imageUploader.addOnFinishHandler(new IUploader.OnFinishUploaderHandler() {
            @Override
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus().equals(IUploadStatus.Status.SUCCESS)) {
                    final String url = uploader.getServerInfo().message;
                    updateEntity.setAttribute(LSDAttribute.IMAGE_URL, url);
                    updateEntity.setAttribute(LSDAttribute.ICON_URL, url);
                    imageUploader.setImageURL(url);
                    // The server sends useful information to the client by default
                    IUploader.UploadedInfo info = uploader.getServerInfo();
//                    System.out.println("File name " + info.name);
//                    System.out.println("File content-type " + info.ctype);
//                    System.out.println("File size " + info.size);
//
//                    // You can send any customized message and parse it
//                    System.out.println("Server message " + info.message);
                } else {
                    Window.alert("Failed to upload image.");
                }
            }


        });


        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                BusFactory.getInstance().send(new UpdateAliasRequest(updateEntity), new AbstractResponseCallback<UpdateAliasRequest>() {
                    @Override
                    public void onSuccess(UpdateAliasRequest message, UpdateAliasRequest response) {
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
            public void onClick(ClickEvent event) {
                try {
                popup.hide();
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });



    }

    public interface ChangeAction {
        void run(LSDEntity newAlias);
    }
}