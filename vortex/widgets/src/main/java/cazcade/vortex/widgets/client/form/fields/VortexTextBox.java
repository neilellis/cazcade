/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public abstract class VortexTextBox extends AbstractVortexFormField {

    @UiField TextBox textBox;
    private int    maxLength = Integer.MAX_VALUE;
    private String oldText   = "";

    @Override public void clear() {
        super.clear();
        textBox.setText("");
        oldText = "";
    }

    @Nullable @Override
    public String getStringValue() {
        return textBox.getText();
    }

    @Override
    protected void initWidget(final Widget widget) {
        super.initWidget(widget);
        validityImage.setResource(Resources.INSTANCE.blank());
    }

    protected String cleanUpText(final String text) {
        return text;
    }

    protected boolean validCharacter(final int keyCode) {
        return true;
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void setTextBox(final TextBox textBox) {
        this.textBox = textBox;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    public void setInputType(final String type) {
        textBox.getElement().setAttribute("type", type);
    }

    public String getValue() {
        return textBox.getText();
    }

    @Override
    public void setValue(final String text) {
        textBox.setText(text);
        processChange();
    }

    @Override
    public void bind(final LSDAttribute attribute, final String prefix, final String initialValue) {
        boundAttribute = attribute;
        setValue(initialValue);
    }

    public int getVisibleLength() {
        return textBox.getVisibleLength();
    }

    public void setVisibleLength(final int length) {
        textBox.setVisibleLength(length);
    }

    protected class CleanUpKeyUpHandler implements KeyUpHandler {
        @Override
        public void onKeyUp(@Nonnull final KeyUpEvent event) {
            String text = textBox.getText();
            if (!oldText.equals(text)) {
                text = cleanUpText(text);
                if (!text.equals(textBox.getText())) {
                    textBox.setText(text);
                }
                oldText = text;
                if (text.length() > 0) {
                    showValidity();
                }
            }

        }
    }

}
