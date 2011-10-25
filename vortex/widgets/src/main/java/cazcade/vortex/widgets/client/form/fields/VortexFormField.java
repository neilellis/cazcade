package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.user.client.ui.Image;

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface VortexFormField extends Bindable {

    String getStringValue();


    boolean isValid();

    Image getValidityImage();

    void setValidityImage(Image validityImage);

    void setShowValidity(boolean showValidity);

    void setErrorMessage(String errorMessage);

    void setValue(String text);

    boolean isMultiValue();

    boolean isCompoundField();

    List<String> getStringValues();

    LSDEntity getEntity();

    /**
     * Returns a stripped down entity with only the information required for an update.
     * @return a lightweight entity.
     */
    LSDEntity getEntityDiff();
}
