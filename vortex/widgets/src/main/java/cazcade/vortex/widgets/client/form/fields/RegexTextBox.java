package cazcade.vortex.widgets.client.form.fields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class RegexTextBox extends VortexTextBox {

    private String regex = "^.*$";
    private String oldText = "";


    interface RegexTextBoxUiBinder extends UiBinder<HTMLPanel, RegexTextBox> {
    }

    private static RegexTextBoxUiBinder ourUiBinder = GWT.create(RegexTextBoxUiBinder.class);

    public RegexTextBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    final int keyCode = event.getUnicodeCharCode();

                    if (isVisibleKeyPress(keyCode)) {

                        if (textBox.getText().length() > getMaxLength()) {
                            textBox.cancelKey();
                        }
                    }

                    if (keyCode == KeyCodes.KEY_ENTER) {
                        callOnChangeAction();
                    }

                }
            }
        });

        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                callOnChangeAction();
            }
        });

//        textBox.addBlurHandler(new BlurHandler() {
//            @Override
//            public void onBlur(BlurEvent event) {
//                callOnChangeAction();
//            }
//        });

        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    String text = textBox.getText();
                    if (!oldText.equals(text)) {
                        text = cleanUpText(text);
                        textBox.setText(text);
                        oldText = text;
                        if (text.length() > 0) {
                            showValidity();
                        }
                    }
                }
            }
        });

    }

    @Override
    protected String cleanUpText(String text) {
        return super.cleanUpText(text);
    }


    @Override
    public boolean isValid() {
        return textBox.getText().matches(regex);
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

}