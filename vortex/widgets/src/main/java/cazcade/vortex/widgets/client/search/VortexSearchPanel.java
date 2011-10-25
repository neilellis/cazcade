package cazcade.vortex.widgets.client.search;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchPanel extends HistoryAwareComposite {

    private Bus bus = BusFactory.getInstance();
    private ResultWidgetStrategy resultWidgetStrategy;

    @Override
    public void onLocalHistoryTokenChanged(String token) {
        search(token);
    }

    interface VortexSearchPanelUiBinder extends UiBinder<HTMLPanel, VortexSearchPanel> {
    }


    public static interface ResultWidgetStrategy {
        Widget getResultWidgetForEntity(LSDEntity subEntity);
    }


    private static VortexSearchPanelUiBinder ourUiBinder = GWT.create(VortexSearchPanelUiBinder.class);

    @UiField
    public VortexSearchBox searchBox;

    @UiField
    public VortexSearchResultList searchResults;

    public VortexSearchPanel(final ResultWidgetStrategy resultWidgetStrategy) {
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
            public void onSuccess(SearchRequest message, SearchRequest response) {
                final List<LSDEntity> subEntities = response.getResponse().getSubEntities(LSDAttribute.CHILD);
                for (LSDEntity subEntity : subEntities) {
                    final Widget widgetForEntity = VortexSearchPanel.this.resultWidgetStrategy.getResultWidgetForEntity(subEntity);
                    if (widgetForEntity != null) {
                        searchResults.addResult(widgetForEntity);
                    }
                }
            }

            @Override
            public void onFailure(SearchRequest message, SearchRequest response) {
            }
        });
    }

}