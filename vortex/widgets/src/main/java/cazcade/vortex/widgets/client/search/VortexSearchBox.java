package cazcade.vortex.widgets.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchBox extends Composite {
    private OnSearchAction onSearchAction;

    public void setOnSearchAction(OnSearchAction onSearchAction) {
        this.onSearchAction = onSearchAction;
    }

    interface VortexSearchBoxUiBinder extends UiBinder<HTMLPanel, VortexSearchBox> {
    }

    public static interface OnSearchAction {
        void onSearch(String search);
    }

    private static VortexSearchBoxUiBinder ourUiBinder = GWT.create(VortexSearchBoxUiBinder.class);

    @UiField
    public SuggestBox suggestionBox;

    public VortexSearchBox() {
        init();
        suggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                onSearchAction.onSearch(stringValueChangeEvent.getValue());
            }
        });


    }

    protected void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}