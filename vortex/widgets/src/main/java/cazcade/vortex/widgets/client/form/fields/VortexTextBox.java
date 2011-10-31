package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public abstract class VortexTextBox extends AbstractVortexFormField {

    private int maxLength = Integer.MAX_VALUE;


    @UiField
    TextBox textBox;
    private String oldText;

    @Override
    protected void initWidget(Widget widget) {
        super.initWidget(widget);
        validityImage.setResource(Resources.INSTANCE.blank());
    }

    protected String cleanUpText(String text) {
        return text;
    }


    protected boolean validCharacter(int keyCode) {
        return true;
    }

    @Override
    public String getStringValue() {
        return textBox.getText();
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void setTextBox(TextBox textBox) {
        this.textBox = textBox;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setInputType(String type) {
        textBox.getElement().setAttribute("type", type);
    }

    public String getValue() {
        return textBox.getText();
    }

    @Override
    public void setValue(String text) {
        textBox.setText(text);
    }

    @Override
    public void bind(LSDAttribute attribute, String prefix, String initialValue) {
        this.boundAttribute = attribute;
        setValue(initialValue);
    }

    public int getVisibleLength() {
        return textBox.getVisibleLength();
    }

    public void setVisibleLength(int length) {
        textBox.setVisibleLength(length);
    }

    protected class CleanUpKeyUpHandler implements KeyUpHandler {
        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (!event.isAnyModifierKeyDown()) {
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

}
