package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class VortexTextArea extends AbstractVortexFormField {

    @Override
    public String getStringValue() {
        return textArea.getValue();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(String text) {
        textArea.setValue(text);
    }

    @Override
    public void bind(LSDAttribute attribute, String prefix, String initialValue) {
        this.boundAttribute = attribute;
        setValue(initialValue);
    }

    interface VortexTextAreaUiBinder extends UiBinder<HTMLPanel, VortexTextArea> {
    }

    private static final VortexTextAreaUiBinder ourUiBinder = GWT.create(VortexTextAreaUiBinder.class);
    @UiField
    TextArea textArea;

    public VortexTextArea() {
        initWidget(ourUiBinder.createAndBindUi(this));
        textArea.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(@Nonnull KeyPressEvent event) {
                final int keyCode = event.getUnicodeCharCode();

                if (keyCode == KeyCodes.KEY_ENTER) {
                    callOnChangeAction();
                }


            }
        });

        textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                callOnChangeAction();
            }
        });

        textArea.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                callOnChangeAction();
            }
        });

        textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                showValidity();
            }
        });
    }
}