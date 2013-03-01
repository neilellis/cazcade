/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.CachingScope;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class VortexMultiListBox extends AbstractVortexFormField {

    @Nonnull
    protected final BusService bus = Bus.get();
    protected boolean useVisibleText;
    protected boolean otherOption;

    @UiField ListBox listBox;

    @UiField RegexTextBox otherBox;

    public boolean isUseVisibleText() {
        return useVisibleText;
    }

    public void setUseVisibleText(final boolean useVisibleText) {
        this.useVisibleText = useVisibleText;
    }


    interface VortexMultiListBoxUiBinder extends UiBinder<HTMLPanel, VortexMultiListBox> {}

    private static final VortexMultiListBoxUiBinder multiListBoxBinder = GWT.create(VortexMultiListBoxUiBinder.class);

    public static UiBinder getUiBinder() {
        return multiListBoxBinder;
    }

    public VortexMultiListBox() {
        super();
        initWidget(multiListBoxBinder.createAndBindUi(this));
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                VortexMultiListBox.this.processChange();
            }
        });
    }

    @Nonnull @Override
    public String getStringValue() {
        //todo: refactor classes so we don't have this mess
        throw new UnsupportedOperationException("This widget does not support single value operations");
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(final String text) {
        throw new UnsupportedOperationException("This widget does not support single value operations");
    }

    public void setVisibleItemCount(final int visibleItemCount) {
        listBox.setVisibleItemCount(visibleItemCount);
    }

    @Nonnull
    public List<String> getStringValues() {
        final List<String> values = new ArrayList<String>();
        final int max = listBox.getItemCount();
        for (int i = 0; i < max; i++) {
            if (listBox.isItemSelected(i)) {
                if (useVisibleText) {
                    values.add(listBox.getItemText(i));
                } else {
                    values.add(listBox.getValue(i));
                }
            }
        }
        if (otherOption) {
            if (listBox.getSelectedIndex() == listBox.getItemCount() - 1) {
                values.add(otherBox.getStringValue());
            }
        }
        return values;
    }

    public void setValues(@Nonnull final List<String> values) {
        final int max = listBox.getItemCount();
        for (int i = 0; i < max; i++) {
            if (useVisibleText) {
                listBox.setItemSelected(i, values.contains(listBox.getItemText(i)));
            } else {
                listBox.setItemSelected(i, values.contains(listBox.getValue(i)));
            }
        }
        for (final String value : values) {
            boolean found = false;
            for (int i = 0; i < max; i++) {
                if (useVisibleText) {
                    if (listBox.getItemText(i).equals(value)) {
                        found = true;
                    }
                } else {
                    if (listBox.getValue(i).equals(value)) {
                        found = true;
                    }
                }
            }
            if (!found) {
                otherBox.setValue(value);
            }
        }
        otherBox.setWidth(listBox.getOffsetWidth() + "px");
    }

    @Override
    public void bind(@Nonnull final Attribute attribute, final String prefix, @Nonnull final List<String> initialValues) {
        boundAttribute = attribute;
        final LURI rootForOptions = new LURI("pool:///sys/cat/" + prefix + "/" + attribute.getKeyName()
                                                                                                    .replace('.', '/'));
        final RetrievePoolRequest retrievePoolRequest = new RetrievePoolRequest(rootForOptions, true, false);
        retrievePoolRequest.setCachingScope(CachingScope.USER);
        bus.send(retrievePoolRequest, new AbstractMessageCallback<RetrievePoolRequest>() {
            @Override
            public void onSuccess(final RetrievePoolRequest original, @Nonnull final RetrievePoolRequest message) {
                final List<TransferEntity> entities = message.response().children(Dictionary.CHILD_A);
                Collections.reverse(entities);
                for (final TransferEntity entity : entities) {
                    listBox.addItem(entity.$(Dictionary.TITLE), entity.$(Dictionary.NAME));
                }
                setValues(initialValues);
            }
        });
    }

    @Override
    public void bind(final Attribute attribute, final String prefix, final String initialValue) {
        throw new UnsupportedOperationException("This widget does not support single value operations");
    }

    @Override
    public boolean isMultiValue() {
        return true;
    }

    public void setOtherOption(final boolean otherOption) {
        this.otherOption = otherOption;
        if (!otherOption) {
            otherBox.removeFromParent();
        }
    }
}
