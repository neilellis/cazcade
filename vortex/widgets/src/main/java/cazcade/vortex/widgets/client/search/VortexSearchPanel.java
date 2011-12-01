package cazcade.vortex.widgets.client.search;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.request.SearchRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchPanel extends HistoryAwareComposite {

    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    private final ResultWidgetStrategy resultWidgetStrategy;

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
        search(token);
    }

    interface VortexSearchPanelUiBinder extends UiBinder<HTMLPanel, VortexSearchPanel> {
    }


    public interface ResultWidgetStrategy {
        @Nonnull
        Widget getResultWidgetForEntity(LSDBaseEntity subEntity);
    }


    private static final VortexSearchPanelUiBinder ourUiBinder = GWT.create(VortexSearchPanelUiBinder.class);

    @UiField
    public VortexSearchBox searchBox;

    @UiField
    public VortexSearchResultList searchResults;

    public VortexSearchPanel(final ResultWidgetStrategy resultWidgetStrategy) {
        super();
        this.resultWidgetStrategy = resultWidgetStrategy;
        init();
        searchBox.setOnSearchAction(new VortexSearchBox.OnSearchAction() {
            @Override
            public void onSearch(final String search) {
                getHistoryManager().addHistory(getHistoryToken(), search);
            }
        });

    }

    protected void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    private void search(final String search) {
        searchResults.clear();
        bus.send(new SearchRequest(search), new AbstractResponseCallback<SearchRequest>() {
            @Override
            public void onSuccess(final SearchRequest message, @Nonnull final SearchRequest response) {
                final List<LSDBaseEntity> subEntities = response.getResponse().getSubEntities(LSDAttribute.CHILD);
                for (final LSDBaseEntity subEntity : subEntities) {
                    final Widget widgetForEntity = resultWidgetStrategy.getResultWidgetForEntity(subEntity);
                    if (widgetForEntity != null) {
                        searchResults.addResult(widgetForEntity);
                    }
                }
            }

            @Override
            public void onFailure(final SearchRequest message, @Nonnull final SearchRequest response) {
            }
        });
    }

}