package cazcade.vortex.widgets.client.search;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class VortexSearchResult extends Composite {
    interface VortexSearchResultUiBinder extends UiBinder<HTMLPanel, VortexSearchResult> {
    }

    private static final VortexSearchResultUiBinder ourUiBinder = GWT.create(VortexSearchResultUiBinder.class);

    @UiField
    Label title;
    @UiField
    Label description;
    @UiField
    Label uri;

    public VortexSearchResult(@Nonnull LSDEntity subEntity) {
        initWidget(ourUiBinder.createAndBindUi(this));
        title.setText(subEntity.getAttribute(LSDAttribute.TITLE));
        description.setText(subEntity.getAttribute(LSDAttribute.DESCRIPTION));
        uri.setText(subEntity.getAttribute(LSDAttribute.URI));
    }
}