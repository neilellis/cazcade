package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 * @codedTo Screamadelica - Primal Scream  http://www.youtube.com/watch?v=zF2YZqHOqL4
 */
public class VortexAddressPicker extends VortexCompoundFormField {

    @Nonnull
    Bus bus = BusFactory.getInstance();

    interface VortexAddressPickerUiBinder extends UiBinder<HTMLPanel, VortexAddressPicker> {
    }

    private static final VortexAddressPickerUiBinder ourUiBinder = GWT.create(VortexAddressPickerUiBinder.class);
    @UiField
    RegexTextBox addressFirstLine;
    @UiField
    RegexTextBox addressSecondLine;
    @UiField
    RegexTextBox city;
    @UiField
    RegexTextBox state;
    @UiField
    RegexTextBox postalCode;
    @UiField
    VortexListBox country;

    public VortexAddressPicker() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void bind(@Nonnull final LSDEntity entity, final LSDAttribute attribute, final String prefix) {
        setEntity(entity.getSubEntity(attribute, false));
        final Map<LSDAttribute, VortexFormField> map = new HashMap<LSDAttribute, VortexFormField>();
        map.put(LSDAttribute.ADDRESS_FIRST_LINE, addressFirstLine);
        map.put(LSDAttribute.ADDRESS_SECOND_LINE, addressSecondLine);
        map.put(LSDAttribute.ADDRESS_CITY, city);
        map.put(LSDAttribute.ADDRESS_STATE, state);
        map.put(LSDAttribute.ADDRESS_POSTALCODE, postalCode);
        map.put(LSDAttribute.ADDRESS_COUNTRY, country);
        bindAll(entity, map, prefix, attribute);
//        addressFirstLine.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_FIRST_LINE));
//        addressSecondLine.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_SECOND_LINE));
//        city.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_CITY));
//        state.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_STATE));
//        postalCode.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_POSTALCODE));
//        country.setAttribute(subEntity.getAttribute(LSDAttribute.ADDRESS_COUNTRY));
    }


}