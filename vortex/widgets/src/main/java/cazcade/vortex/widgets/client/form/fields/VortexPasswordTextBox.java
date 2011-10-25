package cazcade.vortex.widgets.client.form.fields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class VortexPasswordTextBox extends VortexTextBox {


    private VortexPasswordTextBox pairedBox;
    public static final int MIN_PASSWORD_LENGTH = 6;


    interface VortexPasswordTextBoxUiBinder extends UiBinder<HTMLPanel, VortexPasswordTextBox> {
    }

    private static VortexPasswordTextBoxUiBinder ourUiBinder = GWT.create(VortexPasswordTextBoxUiBinder.class);

    public VortexPasswordTextBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                final int keyCode = event.getUnicodeCharCode();

                if (keyCode == KeyCodes.KEY_ENTER) {
                    onChangeAction.run();
                }
            }
        });

        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                showValidity();
            }
        });
        textBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showValidity();
            }
        });
    }

    public boolean isValid() {
        if (!showValidityFlag) {
            return true;
        }
        if(pairedBox != null && pairedBox.getStringValue() != null && !pairedBox.getStringValue().isEmpty() && !pairedBox.getStringValue().equals(getStringValue())) {
            errorMessage.setText("Passwords don't match");
            return false;
        } else {
            errorMessage.setText("Too simple, letters & numbers please.");
        }
        String text = textBox.getText();
        return text.length() != 0 && text.matches(".*[^a-zA-Z].*") && text.matches(".*[a-zA-Z].*") && text.length() > MIN_PASSWORD_LENGTH;

    }



    public String getStringValue() {
        return textBox.getText();
    }

    public void setPairedBox(VortexPasswordTextBox pairedBox) {
        this.pairedBox = pairedBox;
    }


}