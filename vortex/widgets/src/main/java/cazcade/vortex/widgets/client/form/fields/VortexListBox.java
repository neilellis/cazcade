package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.LiquidCachingScope;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class VortexListBox extends AbstractVortexFormField {

    @Nonnull
    public static final String OTHER_FIELD_VALUE = "_______OTHER_________";
    @Nonnull
    protected final Bus bus = BusFactory.getInstance();

    protected boolean useVisibleText;
    protected boolean otherOption;
    private boolean cacheOptions;
    private String value;

    @Override
    public String getStringValue() {
        if (otherOption) {
            if (listBox.getSelectedIndex() == listBox.getItemCount() - 1) {
                return otherBox.getStringValue();
            }
        }
        if (useVisibleText) {
            return listBox.getItemText(listBox.getSelectedIndex());
        } else {
            return listBox.getValue(listBox.getSelectedIndex());
        }

    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(@Nullable final String text) {
        if (text == null || text.isEmpty()) {
            listBox.setItemSelected(0, true);
            otherBox.addStyleName("invisible");
            otherBox.setValue("");
            return;
        }
        boolean found = false;
        final int max = listBox.getItemCount();
        for (int i = 0; i < max; i++) {
            if (useVisibleText) {
                if (listBox.getItemText(i).equals(text)) {
                    listBox.setSelectedIndex(i);
                    found = true;
                }
            } else {
                if (listBox.getValue(i).equals(text)) {
                    listBox.setSelectedIndex(i);
                    found = true;
                }
            }
        }
        if (otherOption && !found) {
            otherBox.setWidth(listBox.getOffsetWidth() + "px");
            otherBox.removeStyleName("invisible");
            listBox.setItemSelected(listBox.getItemCount() - 1, true);
            otherBox.setValue(text);
        }
    }


    public boolean isUseVisibleText() {
        return useVisibleText;
    }

    public void setUseVisibleText(final boolean useVisibleText) {
        this.useVisibleText = useVisibleText;
    }

    public void setCacheOptions(final boolean cacheOptions) {
        this.cacheOptions = cacheOptions;
    }


    interface VortexListBoxUiBinder extends UiBinder<HTMLPanel, VortexListBox> {
    }

    private static final VortexListBoxUiBinder listBoxBinder = GWT.create(VortexListBoxUiBinder.class);

    public static UiBinder getUiBinder() {
        return listBoxBinder;
    }

    @UiField
    ListBox listBox;

    @UiField
    RegexTextBox otherBox;

    public VortexListBox() {
        super();
        initWidget((Widget) getUiBinder().createAndBindUi(this));
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                if (otherOption) {
                    if (listBox.getSelectedIndex() == listBox.getItemCount() - 1) {
                        otherBox.removeStyleName("invisible");
                    } else {
                        otherBox.addStyleName("invisible");
                    }
                }
                callOnChangeAction();
            }
        });
        listBox.addItem("Choose ...", "");

    }

    @Override
    public void bind(@Nonnull final LSDAttribute attribute, final String prefix, final String initialValue) {
        boundAttribute = attribute;
        final LiquidURI rootForOptions = new LiquidURI("pool:///sys/cat/" + prefix + "/" + attribute.getKeyName().replace('.', '/'));
        final RetrievePoolRequest retrievePoolRequest = new RetrievePoolRequest(rootForOptions, LiquidRequestDetailLevel.TITLE_AND_NAME, true, false);
        retrievePoolRequest.setCachingScope(LiquidCachingScope.USER);
        bus.send(retrievePoolRequest, new AbstractResponseCallback<RetrievePoolRequest>() {
            @Override
            public void onSuccess(final RetrievePoolRequest message, @Nonnull final RetrievePoolRequest response) {
                final List<LSDTransferEntity> entities = response.getResponse().getSubEntities(LSDAttribute.CHILD);
                Collections.reverse(entities);
                for (final LSDTransferEntity entity : entities) {
                    listBox.addItem(entity.getAttribute(LSDAttribute.TITLE), entity.getAttribute(LSDAttribute.NAME));
                }
                if (otherOption) {
                    listBox.addItem("Other (please specify)", OTHER_FIELD_VALUE);
                }
                setValue(initialValue);
            }
        });

    }

    @Override
    public void setOnChangeAction(final Runnable onChangeAction) {
        super.setOnChangeAction(onChangeAction);
        otherBox.setOnChangeAction(onChangeAction);
    }

    public void setOtherOption(final boolean otherOption) {
        this.otherOption = otherOption;
        if (!otherOption) {
            otherBox.removeFromParent();
        }
    }
}