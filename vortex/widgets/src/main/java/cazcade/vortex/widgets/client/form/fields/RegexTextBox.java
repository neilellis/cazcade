/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RegexTextBox extends VortexTextBox {

    private String regex = "^.*$";


    interface RegexTextBoxUiBinder extends UiBinder<HTMLPanel, RegexTextBox> {}

    private static final RegexTextBoxUiBinder ourUiBinder = GWT.create(RegexTextBoxUiBinder.class);

    public RegexTextBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(@Nonnull final KeyPressEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    final int keyCode = event.getUnicodeCharCode();

                    if (isVisibleKeyPress(keyCode)) {

                        if (textBox.getText().length() > getMaxLength()) {
                            textBox.cancelKey();
                        }
                    }

                    if (keyCode == KeyCodes.KEY_ENTER) {
                        processChange();
                    }

                }
            }
        });

        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> stringValueChangeEvent) {
                processChange();
            }
        });

        //        textBox.addBlurHandler(new BlurHandler() {
        //            @Override
        //            public void onBlur(BlurEvent event) {
        //                processChange();
        //            }
        //        });

        textBox.addKeyUpHandler(new CleanUpKeyUpHandler());

    }

    @Override
    protected String cleanUpText(final String text) {
        return super.cleanUpText(text);
    }


    @Override
    public boolean isValid() {
        return textBox.getText().matches(regex);
    }

    public void setRegex(final String regex) {
        this.regex = regex;
    }


}