package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class VortexDateBox extends AbstractVortexFormField{
    @Override
    public String getStringValue() {
        return String.valueOf(dateBox.getValue().getTime());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(String text) {
        dateBox.setValue(new Date(Long.parseLong(text)));
    }

    @Override
    public void bind(LSDAttribute attribute, String prefix, String initialValue) {
        this.boundAttribute= attribute;
        DateTimeFormat dateFormat = DateTimeFormat.getLongDateFormat();
        dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));

    }

    interface VortexDateBoxUiBinder extends UiBinder<HTMLPanel, VortexDateBox> {
    }

    private static VortexDateBoxUiBinder ourUiBinder = GWT.create(VortexDateBoxUiBinder.class);
    @UiField
    DateBox dateBox;

    public VortexDateBox() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}