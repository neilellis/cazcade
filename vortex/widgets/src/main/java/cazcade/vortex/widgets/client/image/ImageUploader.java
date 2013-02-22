/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditFinishHandler;
import cazcade.vortex.common.client.events.EditStartEvent;
import cazcade.vortex.common.client.events.EditStartHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class ImageUploader extends Composite {


    public static enum Status {SUCCESS, FAILURE}

    ;

    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, ImageUploader> {}

    private static final ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);
    private Status status;
    private String imageUrl;


    public ImageUploader() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));


    }

    @Override protected void onAttach() {
        super.onAttach();
        pick();
    }

    public Status getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOnStartUploadHandler(EditStartHandler onStartUploadHandler) {
        addHandler(onStartUploadHandler, EditStartEvent.TYPE);
    }

    public void setOnFinishHandler(EditFinishHandler onFinishHandler) {
        addHandler(onFinishHandler, EditFinishEvent.TYPE);
    }

    private void setStatusByName(final String name) {
        status = Status.valueOf(name);
    }

    private void finish() {
        fireEvent(new EditFinishEvent());
    }

    private void error() {
    }

    private void start() {
        fireEvent(new EditStartEvent());
    }

    public native void pick() /*-{
        this.@cazcade.vortex.widgets.client.image.ImageUploader::start()();
        this.@cazcade.vortex.widgets.client.image.ImageUploader::setImageUrl(Ljava/lang/String;)('');
        var gwtThis = this;
        $wnd.filepicker.pick({
                mimetypes: ['image/*'],
                container: 'image-uploader-filepicker-frame',
                services: ['COMPUTER', 'FACEBOOK', 'FLICKR', 'DROPBOX', 'WEBCAM', 'INSTAGRAM', 'PICASA', 'URL', 'IMAGE_SEARCH']
            },
            function (FPFile) {
                console.log(JSON.stringify(FPFile));
                var url;
                if (FPFile.url.indexOf('https') == 0) {
                    url = 'http' + FPFile.url.substring(5);
                } else {
                    url = FPFile.url;
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


    public void clear() {

    }
}