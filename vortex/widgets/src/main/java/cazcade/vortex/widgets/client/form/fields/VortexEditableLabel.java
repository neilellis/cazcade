/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class VortexEditableLabel extends AbstractVortexFormField {

    private final int maxLength = Integer.MAX_VALUE;
    private boolean readonly;

    public void setWordwrap(final boolean wordwrap) {
        if (wordwrap) {
            container.addClassName("word-wrap");
        }
        else {
            container.removeClassName("word-wrap");
        }
        hoverEdit.setVisible(!wordwrap);
        label.setWordWrap(wordwrap);
    }

    public void setPrefix(final String prefix) {
        label.setPrefix(prefix);
    }

    public void setFormat(final boolean format) {
        if (format) {
            label.setFormatter(FormatUtil.getInstance());
        }
        else {
            label.setFormatter(null);
        }
    }

    public void addClickHandler(final ClickHandler clickHandler) {
        label.addDomHandler(clickHandler, ClickEvent.getType());
    }

    interface VortexEditableLabelUiBinder extends UiBinder<HTMLPanel, VortexEditableLabel> {}

    private static final VortexEditableLabelUiBinder ourUiBinder = GWT.create(VortexEditableLabelUiBinder.class);

    public VortexEditableLabel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        label.setOnEditEndAction(new Runnable() {
            @Override
            public void run() {
                onChange();
            }
        });

    }

    @UiField EditableLabel label;
    @UiField Label         hoverEdit;
    @UiField SpanElement   container;

    @UiHandler("hoverEdit") void onHoverEditClick(final ClickEvent e) {
        label.startEdit();
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

    @Nullable @Override
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

    public void setLabel(final EditableLabel label) {
        this.label = label;
    }

    public int getMaxLength() {
        return label.getMaxLength();
    }

    public void setVisibleLines(final int number) {
        label.setVisibleLines(number);
    }

    public void setVisibleLength(final int length) {
        label.setVisibleLength(length);
    }

    public void setHorizontalAlignment(final HasHorizontalAlignment.HorizontalAlignmentConstant align) {
        label.setHorizontalAlignment(align);
    }

    public void setShowBrief(final boolean b) {
        label.setShowBrief(b);
    }


    public void setMaxLength(final int maxLength) {
        label.setMaxLength(maxLength);
    }

    public void setInputType(final String type) {
        label.setInputType(type);
    }

    @Nullable
    public String getValue() {
        return label.getText();
    }

    @Override
    public void setValue(@Nonnull final String text) {
        label.setText(text);
    }

    @Override
    public void bind(final LSDAttribute attribute, final String prefix, @Nonnull final String initialValue) {
        boundAttribute = attribute;
        setValue(initialValue);
    }

    @Override void setEditable(final boolean editable) {
        if (!readonly) {
            label.setEditable(editable);
            if (editable) {
                container.addClassName("editable");
            }
            else {
                container.removeClassName("editable");
            }
            hoverEdit.setVisible(editable);
        }
    }

    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
        if (readonly) {
            container.addClassName("readonly");
            container.removeClassName("editable");
            label.setEditable(false);
            hoverEdit.setVisible(false);
        }
        else {
            container.removeClassName("readonly");
        }

    }


    public String getPlaceholder() {
        return label.getPlaceholder();
    }

    public void setPlaceholder(final String placeholder) {
        label.setPlaceholder(placeholder);
    }
}
