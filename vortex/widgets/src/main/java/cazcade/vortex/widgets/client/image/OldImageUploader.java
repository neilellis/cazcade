package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
public class OldImageUploader extends Composite {

    @Nonnull
    private final SingleUploader defaultUploader;


    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, OldImageUploader> {
    }

    private static final ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);
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

    public OldImageUploader() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        defaultUploader = new SingleUploader();
        defaultUploader.setServletPath("/_image-upload");
        defaultUploader.setAvoidRepeatFiles(false);
        defaultUploader.setAutoSubmit(true);
        defaultUploader.setStatusWidget(new UploadStatusHandler());

        loaderPanel.add(defaultUploader);
        final CachedImage cachedImage = new CachedImage();
        cachedImage.setWidth("300px");
        cachedImage.setHeight("200px");
        imageLoadedPanel.add(cachedImage);
    }


    public void setImageURL(@Nullable final String url) {
        if (url != null && !url.isEmpty()) {
            changeImage(new Image(url));
        }
    }

    public HandlerRegistration addOnCancelUploadHandler(final IUploader.OnCancelUploaderHandler handler) {
        return defaultUploader.addOnCancelUploadHandler(handler);
    }

    public HandlerRegistration addOnChangeUploadHandler(final IUploader.OnChangeUploaderHandler handler) {
        return defaultUploader.addOnChangeUploadHandler(handler);
    }

    public HandlerRegistration addOnFinishUploadHandler(final IUploader.OnFinishUploaderHandler handler) {
        return defaultUploader.addOnFinishUploadHandler(handler);
    }

    public HandlerRegistration addOnStartUploadHandler(final IUploader.OnStartUploaderHandler handler) {
        return defaultUploader.addOnStartUploadHandler(handler);
    }

    public HandlerRegistration addOnStatusChangedHandler(final IUploader.OnStatusChangedHandler handler) {
        return defaultUploader.addOnStatusChangedHandler(handler);
    }

    public void addOnFinishHandler(final IUploader.OnFinishUploaderHandler onFinishUploaderHandler) {
        defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
    }


    private void changeImage(@Nonnull final Image image) {
        statusText.setInnerText("Processing");
        spinner.setVisible(false);

        final CachedImage cachedImage = new CachedImage();
        cachedImage.setCached(!BrowserUtil.isImage(image.getUrl()));
        cachedImage.setWidth("300px");
        cachedImage.setHeight("200px");
        if (imageLoadedPanel.getWidgetCount() > 0) {
            WidgetUtil.swap(imageLoadedPanel.getWidget(0), cachedImage);
//                imageLoadedPanel.addAndReplaceElement(cachedImage, imageLoadedPanel.getWidget(0).getElement().<Element>cast());
        } else {
            imageLoadedPanel.add(cachedImage);
        }
        cachedImage.setSize(CachedImage.MEDIUM);
        cachedImage.setUrl(image.getUrl());
    }

    private class UploadStatusHandler implements IUploadStatus {
        private Status status;
        private UploadStatusChangedHandler handler;
        @Nullable
        private UploadCancelHandler cancelHandler;

        @Nullable
        @Override
        public HandlerRegistration addCancelHandler(final UploadCancelHandler handler) {
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

        @Nonnull
        @Override
        public IUploadStatus newInstance() {
            return new UploadStatusHandler();
        }

        @Override
        public void setCancelConfiguration(final Set<CancelBehavior> config) {
            //TODO
        }

        @Override
        public void setError(final String error) {
            statusWidget.getElement().setInnerText(error);
        }

        @Override
        public void setFileName(final String name) {
        }

        @Override
        public void setI18Constants(final UploadStatusConstants strs) {
        }

        @Override
        public void setStatus(final Status status) {
            if (status == Status.ERROR) {
                statusWidget.addStyleName("error");
            }
            this.status = status;
            handler.onStatusChanged(this);
        }

        @Override
        public void setStatusChangedHandler(final UploadStatusChangedHandler handler) {

            this.handler = handler;
        }

        @Override
        public void setVisible(final boolean visible) {
            statusWidget.setVisible(visible);
        }

        @Override
        public void setProgress(final int done, final int total) {
            if (total > 0 && done != total) {
                statusText.setInnerText((int) ((double) done / (double) total * 100) + "%");
            }
            if (done == total && total > 0) {
                statusText.setInnerText("Processing");
            }
            spinner.setVisible(true);

        }
    }
}