package cazcade.vortex.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author neilellis@cazcade.com
 */
public interface Resources extends ClientBundle {
    public static final Resources INSTANCE =  GWT.create(Resources.class);

    @Source("blank.png")
    ImageResource blank();

    @Source("user_available.png")
    ImageResource userAvailable();

    @Source("user_not_available.png")
    ImageResource userNotAvailable();

    @Source("valid.png")
    ImageResource validFormValueImage();

    @Source("invalid.png")
    ImageResource invalidFormValueImage();
}
