package cazcade.boardcast.client.main.custom;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.CustomObjectEditor;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
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
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;

/**
 * @author neilellis@cazcade.com
 */
public class HashboCustomObjectEditor extends Composite implements CustomObjectEditor {

    private LSDEntity updateEntity;
    private boolean sizeDirty;

    @Override
    public void show(LSDEntity object) {
        updateEntity = object.asUpdateEntity();
        imageUploader.setImageURL(object.getAttribute(LSDAttribute.IMAGE_URL));
        LSDEntity view = object.getSubEntity(LSDAttribute.VIEW, false);
        widthField.setValue(view.getAttribute(LSDAttribute.VIEW_WIDTH));
        heightField.setValue(view.getAttribute(LSDAttribute.VIEW_HEIGHT));
        Runnable sizeDirtyAction = new Runnable() {
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
            public void setPosition(int offsetWidth, int offsetHeight) {
                popup.setPopupPosition(Window.getClientWidth() / 2 - offsetWidth / 2, Window.getClientHeight() / 2 - offsetHeight / 2);
            }
        });
    }

    @Override
    public void setOnChangeAction(ChangeAction onChangeAction) {
        this.onChangeAction = onChangeAction;
    }


    interface EditorUiBinder extends UiBinder<HTMLPanel, HashboCustomObjectEditor> {
    }

    private static EditorUiBinder ourUiBinder = GWT.create(EditorUiBinder.class);

    private ChangeAction onChangeAction;

    @UiField
    PopupPanel popup;

    @UiField
    ImageUploader imageUploader;

    @UiField
    Label changeButton;

    @UiField
    Label cancelButton;
    @UiField
    RegexTextBox widthField;
    @UiField
    RegexTextBox heightField;
    @UiField
    VortexTextArea scriptField;

    public HashboCustomObjectEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        imageUploader.addOnFinishHandler(new IUploader.OnFinishUploaderHandler() {
            @Override
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus().equals(IUploadStatus.Status.SUCCESS)) {
                    final String url = uploader.getServerInfo().message;
                    updateEntity.setAttribute(LSDAttribute.IMAGE_URL, url);
                    updateEntity.setAttribute(LSDAttribute.ICON_URL, url);
                    imageUploader.setImageURL(url);
                } else {
                    Window.alert("Failed to upload image.");
                }
            }


        });


        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (sizeDirty) {
                    BusFactory.getInstance().send(new ResizePoolObjectRequest(updateEntity.getURI(), Integer.parseInt(widthField.getValue()) * 40, Integer.parseInt(heightField.getValue()) * 40), new AbstractResponseCallback<ResizePoolObjectRequest>() {
                        @Override
                        public void onSuccess(ResizePoolObjectRequest message, ResizePoolObjectRequest response) {
                        }
                    });
                }
                BusFactory.getInstance().send(new UpdatePoolObjectRequest(updateEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolObjectRequest message, UpdatePoolObjectRequest response) {
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
            public void onClick(ClickEvent event) {
                try {
                    popup.hide();
                } catch (Exception e) {
                    ClientLog.log("Mysterious popup.hide() exception." + e.getMessage());
                }
            }
        });

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        }, ClickEvent.getType());


    }


}