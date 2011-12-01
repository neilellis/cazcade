package cazcade.vortex.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface Resources extends ClientBundle {
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Nonnull
    @Source("blank.png")
    ImageResource blank();

    @Nonnull
    @Source("user_available.png")
    ImageResource userAvailable();

    @Nonnull
    @Source("user_not_available.png")
    ImageResource userNotAvailable();

    @Nonnull
    @Source("valid.png")
    ImageResource validFormValueImage();

    @Nonnull
    @Source("invalid.png")
    ImageResource invalidFormValueImage();
}
