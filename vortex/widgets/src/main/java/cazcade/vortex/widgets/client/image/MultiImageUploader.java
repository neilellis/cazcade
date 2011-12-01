package cazcade.vortex.widgets.client.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import javax.annotation.Nonnull;
import java.util.ArrayList;


/**
 * @author neilellis@cazcade.com
 */
public class MultiImageUploader extends Composite {

    @Nonnull
    private final ArrayList<String> urls = new ArrayList<String>();
    private Runnable onSuccess;

    public void hide() {
        popup.hide();
    }

    public void show() {
        urls.clear();
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                popup.setPopupPosition(Window.getClientWidth() / 2 - offsetWidth / 2, Window.getClientHeight() / 2 - offsetHeight / 2);
            }
        });
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, MultiImageUploader> {
    }

    private static final ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);


    @UiField
    HTMLPanel loaderPanel;
    @UiField
    HTMLPanel imageLoadedPanel;
    @UiField
    PopupPanel popup;
    @UiField
    Button addButton;
    @UiField
    Label cancelLink;

    public MultiImageUploader() {
        initWidget(ourUiBinder.createAndBindUi(this));
        MultiUploader defaultUploader = new MultiUploader();
        loaderPanel.add(defaultUploader);
        defaultUploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {
            @Override
            public void onFinish(@Nonnull IUploader uploader) {
                if (uploader.getStatus() == IUploadStatus.Status.SUCCESS) {
                    IUploader.UploadedInfo info = uploader.getServerInfo();
                    final String[] returnedUrls = info.message.split(",");
                    for (String url : returnedUrls) {
                        imageLoadedPanel.add(new Image(url));
                        urls.add(url);
                    }

                    // The server sends useful information to the client by default
                    System.out.println("File name " + info.name);
                    System.out.println("File content-type " + info.ctype);
                    System.out.println("File size " + info.size);
                    // You can send any customized message and parse it
                    System.out.println("Server message " + info.message);
                }
            }


        });
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
                onSuccess.run();
            }
        });

        cancelLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
                urls.clear();
            }
        });


    }

    @Nonnull
    public ArrayList<String> getUrls() {
        return urls;
    }
}