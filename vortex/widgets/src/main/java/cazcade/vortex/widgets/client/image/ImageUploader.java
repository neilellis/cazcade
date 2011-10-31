package cazcade.vortex.widgets.client.image;

import cazcade.vortex.gwt.util.client.WidgetUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.SingleUploader;

/**
 * @author neilellis@cazcade.com
 */
public class ImageUploader extends Composite {

    private SingleUploader defaultUploader;

    public PreloadedImage.OnLoadPreloadedImageHandler getShowImage() {
        return showImage;
    }

    interface ImageUploaderUiBinder extends UiBinder<HTMLPanel, ImageUploader> {
    }

    private static ImageUploaderUiBinder ourUiBinder = GWT.create(ImageUploaderUiBinder.class);
    @UiField
    HTMLPanel loaderPanel;
    @UiField
    HTMLPanel imageLoadedPanel;

    public ImageUploader() {
        initWidget(ourUiBinder.createAndBindUi(this));
        defaultUploader = new SingleUploader();
        defaultUploader.setAvoidRepeatFiles(false);
        defaultUploader.setAutoSubmit(true);
        final BaseUploadStatus stat = new BaseUploadStatus();
        defaultUploader.setStatusWidget(stat);
        loaderPanel.add(defaultUploader);
        loaderPanel.add(stat.getWidget());
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

    // Attach an image to the pictures viewer
    private PreloadedImage.OnLoadPreloadedImageHandler showImage = new PreloadedImage.OnLoadPreloadedImageHandler() {
        public void onLoad(PreloadedImage image) {
            changeImage(image);

        }
    };

    private void changeImage(Image image) {
        CachedImage cachedImage = new CachedImage(image);
        if (imageLoadedPanel.getWidgetCount() > 0) {
            WidgetUtil.swap(imageLoadedPanel.getWidget(0), cachedImage);
//                imageLoadedPanel.addAndReplaceElement(cachedImage, imageLoadedPanel.getWidget(0).getElement().<Element>cast());
        } else {
            imageLoadedPanel.add(cachedImage);
        }
    }
}