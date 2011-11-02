package cazcade.vortex.widgets.client.image;

import cazcade.vortex.gwt.util.client.WidgetUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
public class ImageUploader extends Composite {

    private SingleUploader defaultUploader;


    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, ImageUploader> {
    }

    private static ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);
    @UiField
    HTMLPanel loaderPanel;
    @UiField
    HTMLPanel imageLoadedPanel;
    @UiField
    HTMLPanel statusWidget;
    @UiField
    SpanElement statusText;
    @UiField
    Image spinner;

    public ImageUploader() {
        initWidget(ourUiBinder.createAndBindUi(this));
        defaultUploader = new SingleUploader();
        defaultUploader.setAvoidRepeatFiles(false);
        defaultUploader.setAutoSubmit(true);
        defaultUploader.setStatusWidget(new UploadStatusHandler());

        loaderPanel.add(defaultUploader);
        imageLoadedPanel.add(new CachedImage());
    }


    public void setImageURL(String url) {
        if (url != null && !url.isEmpty()) {
            changeImage(new Image(url));
        }
    }

    public void addOnFinishHandler(IUploader.OnFinishUploaderHandler onFinishUploaderHandler) {
        defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
    }


    private void changeImage(Image image) {
        statusText.setInnerText("Loaded");
        spinner.setVisible(false);

        CachedImage cachedImage = new CachedImage(image, CachedImage.MEDIUM);
        if (imageLoadedPanel.getWidgetCount() > 0) {
            WidgetUtil.swap(imageLoadedPanel.getWidget(0), cachedImage);
//                imageLoadedPanel.addAndReplaceElement(cachedImage, imageLoadedPanel.getWidget(0).getElement().<Element>cast());
        } else {
            imageLoadedPanel.add(cachedImage);
        }
    }

    private class UploadStatusHandler implements IUploadStatus {
        private Status status;
        private UploadStatusChangedHandler handler;
        private UploadCancelHandler cancelHandler;

        @Override
        public HandlerRegistration addCancelHandler(UploadCancelHandler handler) {
            cancelHandler = handler;
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    cancelHandler = null;
                }
            };
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public Widget getWidget() {
            return statusWidget;
        }

        @Override
        public IUploadStatus newInstance() {
            return new UploadStatusHandler();
        }

        @Override
        public void setCancelConfiguration(Set<CancelBehavior> config) {
            //TODO
        }

        @Override
        public void setError(String error) {
            statusWidget.getElement().setInnerText(error);
        }

        @Override
        public void setFileName(String name) {
        }

        @Override
        public void setI18Constants(UploadStatusConstants strs) {
        }

        @Override
        public void setStatus(Status status) {
            if (status == Status.ERROR) {
                statusWidget.addStyleName("error");
            }
            this.status = status;
            handler.onStatusChanged(this);
        }

        @Override
        public void setStatusChangedHandler(UploadStatusChangedHandler handler) {

            this.handler = handler;
        }

        @Override
        public void setVisible(boolean visible) {
            statusWidget.setVisible(visible);
        }

        @Override
        public void setProgress(int done, int total) {
            if (total > 0 && done != total) {
                statusText.setInnerText(((int) (((double) done / (double) total) * 100)) + "%");
            }
            if (done == total && total > 0) {
                statusText.setInnerText("Loaded");
            }
            spinner.setVisible(true);

        }
    }
}