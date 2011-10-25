package cazcade.vortex.widgets.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchResultList extends Composite {

    public void clear() {
        for (final Widget widget : resultPanel) {
            widget.addStyleName("invisible");
            new Timer() {
                @Override
                public void run() {
                    widget.removeFromParent();
                }
            }.schedule(300);
        }
    }

    public void addResult(final Widget result) {
        result.addStyleName("invisible");
        resultPanel.add(result);
        new Timer() {
            @Override
            public void run() {
                result.removeStyleName("invisible");
            }
        }.schedule(200);
    }

    interface VortexSearchResultListUiBinder extends UiBinder<HTMLPanel, VortexSearchResultList> {
    }

    private static VortexSearchResultListUiBinder ourUiBinder = GWT.create(VortexSearchResultListUiBinder.class);

    @UiField
    public HTMLPanel resultPanel;

    public VortexSearchResultList() {
        init();

    }

    protected void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}