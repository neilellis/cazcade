package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.Resources;
import cazcade.vortex.widgets.client.misc.EditableLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class VortexEditableLabel extends AbstractVortexFormField {

    private int maxLength = Integer.MAX_VALUE;
    private boolean readonly;

    public void setWordwrap(boolean wordwrap) {
        if (wordwrap) {
            container.addClassName("word-wrap");
        } else {
            container.removeClassName("word-wrap");
        }
        hoverEdit.setVisible(!wordwrap);
        label.setWordWrap(wordwrap);
    }

    public void setPrefix(String prefix) {
        this.label.setPrefix(prefix);
    }

    public void setFormat(boolean format) {
        if (format) {
            label.setFormatter(FormatUtil.getInstance());
        } else {
            label.setFormatter(null);
        }
    }

    public void addClickHandler(ClickHandler clickHandler) {
        label.addDomHandler(clickHandler, ClickEvent.getType());
    }

    interface VortexEditableLabelUiBinder extends UiBinder<HTMLPanel, VortexEditableLabel> {
    }

    private static VortexEditableLabelUiBinder ourUiBinder = GWT.create(VortexEditableLabelUiBinder.class);

    public VortexEditableLabel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setOnEditEndAction(new Runnable() {
            @Override
            public void run() {
                callOnChangeAction();
            }
        });

    }

    @UiField
    EditableLabel label;
    @UiField
    Label hoverEdit;
    @UiField
    SpanElement container;

    @UiHandler("hoverEdit")
    void onHoverEditClick(ClickEvent e) {
        label.startEdit();
    }

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
        return label.getText();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public EditableLabel getLabel() {
        return label;
    }

    public void setLabel(EditableLabel label) {
        this.label = label;
    }

    public int getMaxLength() {
        return label.getMaxLength();
    }

    public void setVisibleLines(int number) {
        label.setVisibleLines(number);
    }

    public void setVisibleLength(int length) {
        label.setVisibleLength(length);
    }

    public void setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant align) {
        label.setHorizontalAlignment(align);
    }

    public void setShowBrief(boolean b) {
        label.setShowBrief(b);
    }


    public void setMaxLength(int maxLength) {
        label.setMaxLength(maxLength);
    }

    public void setInputType(String type) {
        label.setInputType(type);
    }

    public String getValue() {
        return label.getText();
    }

    @Override
    public void setValue(String text) {
        label.setText(text);
    }

    @Override
    public void bind(LSDAttribute attribute, String prefix, String initialValue) {
        this.boundAttribute = attribute;
        setValue(initialValue);
    }

    @Override
    void setEditable(boolean editable) {
        if (!readonly) {
            label.setEditable(editable);
        }
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        if (readonly) {
            label.setEditable(false);
        }
        if (readonly) {
            container.addClassName("readonly");
            container.removeClassName("editable");
            hoverEdit.setVisible(false);
        } else {
            container.addClassName("editable");
            container.removeClassName("readonly");
            hoverEdit.setVisible(true);
        }

    }


    public String getPlaceholder() {
        return label.getPlaceholder();
    }

    public void setPlaceholder(String placeholder) {
        label.setPlaceholder(placeholder);
    }
}
