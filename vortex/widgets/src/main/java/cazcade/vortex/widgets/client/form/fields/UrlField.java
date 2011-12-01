package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class UrlField extends Composite implements VortexFormField {


    interface ChangeImageUrlPanelUiBinder extends UiBinder<HTMLPanel, UrlField> {
    }

    private static final ChangeImageUrlPanelUiBinder ourUiBinder = GWT.create(ChangeImageUrlPanelUiBinder.class);

    @Override
    public boolean isValid() {
        return urlField.isValid();
    }

    @UiField
    RegexTextBox urlField;
    @UiField
    CachedImage previewImage;

    public UrlField() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
//
//        imageUploader.addOnFinishHandler(new IUploader.OnFinishUploaderHandler() {
//            @Override
//            public void onFinish(IUploader uploader) {
//                if (uploader.getStatus().equals(IUploadStatus.Status.SUCCESS)) {
//                    setValue(uploader.getServerInfo().message);
//                    callOnChangeAction();
//                    IUploader.UploadedInfo info = uploader.getServerInfo();
//                } else {
//                    Window.alert("Failed to upload image.");
//                }
//            }
//
//
//        });
    }


    @Override
    public void setValue(final String imageUrl) {
        previewImage.setUrl(imageUrl);
        urlField.setValue(imageUrl);
    }


    public void callOnChangeAction() {
        urlField.callOnChangeAction();
    }

    @Override
    public void setOnChangeAction(@Nonnull final Runnable onChangeAction) {
        urlField.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                onChangeAction.run();
                previewImage.setUrl(urlField.getValue());
            }
        });
    }


    @Override
    public Image getValidityImage() {
        return urlField.getValidityImage();
    }

    @Override
    public void setValidityImage(final Image validityImage) {
        urlField.setValidityImage(validityImage);
    }

    @Override
    public void setShowValidity(final boolean showValidity) {
        urlField.setShowValidity(showValidity);
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        urlField.setErrorMessage(errorMessage);
    }

    @Override
    public void bind(@Nonnull final LSDEntity entity, final LSDAttribute attribute, final String prefix) {
        urlField.bind(entity, attribute, prefix);
        previewImage.setUrl(urlField.getValue());
    }
//
//    public void setEditable(boolean editable) {
//        urlField.setEditable(editable);
//    }

    @Override
    public boolean isMultiValue() {
        return urlField.isMultiValue();
    }

    @Override
    public boolean isCompoundField() {
        return urlField.isCompoundField();
    }

    @Nonnull
    @Override
    public List<String> getStringValues() {
        return urlField.getStringValues();
    }


    @Override
    public LSDEntity getEntity() {
        return urlField.getEntity();
    }

    @Nonnull
    @Override
    public LSDEntity getEntityDiff() {
        return urlField.getEntityDiff();
    }

    @Override
    public String getStringValue() {
        return urlField.getValue();
    }


}