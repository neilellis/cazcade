/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
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

    @Nonnull BusService bus = Bus.get();

    interface VortexAddressPickerUiBinder extends UiBinder<HTMLPanel, VortexAddressPicker> {}

    private static final VortexAddressPickerUiBinder ourUiBinder = GWT.create(VortexAddressPickerUiBinder.class);
    @UiField RegexTextBox  addressFirstLine;
    @UiField RegexTextBox  addressSecondLine;
    @UiField RegexTextBox  city;
    @UiField RegexTextBox  state;
    @UiField RegexTextBox  postalCode;
    @UiField VortexListBox country;

    public VortexAddressPicker() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void bind(@Nonnull final TransferEntity entity, final Attribute attribute, final String prefix) {
        final TransferEntity subEntity = (TransferEntity) entity.child(attribute, false);
        setEntity(subEntity);
        final Map<Attribute, VortexFormField> map = new HashMap<Attribute, VortexFormField>();
        map.put(Dictionary.ADDRESS_FIRST_LINE, addressFirstLine);
        map.put(Dictionary.ADDRESS_SECOND_LINE, addressSecondLine);
        map.put(Dictionary.ADDRESS_CITY, city);
        map.put(Dictionary.ADDRESS_STATE, state);
        map.put(Dictionary.ADDRESS_POSTALCODE, postalCode);
        map.put(Dictionary.ADDRESS_COUNTRY, country);
        bindAll(entity, map, prefix, attribute);
        //        addressFirstLine.$(subEntity.$(Attribute.ADDRESS_FIRST_LINE));
        //        addressSecondLine.$(subEntity.$(Attribute.ADDRESS_SECOND_LINE));
        //        city.$(subEntity.$(Attribute.ADDRESS_CITY));
        //        state.$(subEntity.$(Attribute.ADDRESS_STATE));
        //        postalCode.$(subEntity.$(Attribute.ADDRESS_POSTALCODE));
        //        country.$(subEntity.$(Attribute.ADDRESS_COUNTRY));
    }


}