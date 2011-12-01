package cazcade.vortex.widgets.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchBox extends Composite {
    private OnSearchAction onSearchAction;

    public void setOnSearchAction(final OnSearchAction onSearchAction) {
        this.onSearchAction = onSearchAction;
    }

    interface VortexSearchBoxUiBinder extends UiBinder<HTMLPanel, VortexSearchBox> {
    }

    public interface OnSearchAction {
        void onSearch(String search);
    }

    private static final VortexSearchBoxUiBinder ourUiBinder = GWT.create(VortexSearchBoxUiBinder.class);

    @UiField
    public SuggestBox suggestionBox;

    public VortexSearchBox() {
        super();
        init();
        suggestionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(@Nonnull final ValueChangeEvent<String> stringValueChangeEvent) {
                onSearchAction.onSearch(stringValueChangeEvent.getValue());
            }
        });


    }

    protected void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}