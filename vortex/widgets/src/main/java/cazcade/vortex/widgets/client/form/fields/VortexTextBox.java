package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author neilellis@cazcade.com
 */
public abstract class VortexTextBox extends AbstractVortexFormField {

    private int maxLength= Integer.MAX_VALUE;


    @UiField
    TextBox textBox;

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
        this.boundAttribute= attribute;
        setValue(initialValue);
    }

    public int getVisibleLength() {
        return textBox.getVisibleLength();
    }

    public void setVisibleLength(int length) {
        textBox.setVisibleLength(length);
    }


}
