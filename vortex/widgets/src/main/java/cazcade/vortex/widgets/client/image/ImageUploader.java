package cazcade.vortex.widgets.client.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class ImageUploader extends Composite {


    private Status status;
    private String imageUrl;
    private OnStartUploadHandler onStartUploadHandler;
    private OnFinishUploadHandler onFinishHandler;

    public Status getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setOnStartUploadHandler(OnStartUploadHandler onStartUploadHandler) {
        this.onStartUploadHandler = onStartUploadHandler;
    }

    public void setOnFinishHandler(OnFinishUploadHandler onFinishHandler) {
        this.onFinishHandler = onFinishHandler;
    }

    public enum Status {SUCCESS, FAILURE}

    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, ImageUploader> {
    }

    private static final ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);

    @UiField
    Button uploadButton;

    private void setStatusByName(final String name) {
        status = Status.valueOf(name);
    }

    private void finish() {
        onFinishHandler.onFinish(this);
        Window.alert("Finish!!");
    }

    private void error() {
    }

    private void start() {
        onStartUploadHandler.onStart(this);
    }

    public ImageUploader() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        uploadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                pick();
            }
        });
    }


    public native void pick() /*-{
        this.@cazcade.vortex.widgets.client.image.ImageUploader::start()();
        this.@cazcade.vortex.widgets.client.image.ImageUploader::setImageUrl(Ljava/lang/String;)('');
        var gwtThis= this;
        $wnd.filepicker.pick({
                    mimetypes: ['image/*'],
                    container: 'window'
                },
                function (FPFile) {
                    console.log(JSON.stringify(FPFile));
                    var url;
                    if(FPFile.url.indexOf('https')==0) {
                         url='http'+FPFile.url.substring(5);
                    } else {
                        url= FPFile.url;
                    }
                    gwtThis.@cazcade.vortex.widgets.client.image.ImageUploader::setImageUrl(Ljava/lang/String;)(url);
                    gwtThis.@cazcade.vortex.widgets.client.image.ImageUploader::setStatusByName(Ljava/lang/String;)('SUCCESS');
                    gwtThis.@cazcade.vortex.widgets.client.image.ImageUploader::finish()();
                },
                function (FPError) {
                    console.log(FPError.toString());
                    gwtThis.@cazcade.vortex.widgets.client.image.ImageUploader::setStatusByName(Ljava/lang/String;)('FAILURE');
                    gwtThis.@cazcade.vortex.widgets.client.image.ImageUploader::error()();
                }
        );
    }-*/;


}