package cazcade.vortex.pool.objects.alias;

import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.profile.AliasDetailPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AliasReferenceView extends PoolObjectView {


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, AliasReferenceView> {
    }


    @UiField
    AliasDetailPanel aliasDetailPanel;

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public void setAliasURI(@Nonnull LiquidURI aliasURI) {
        aliasDetailPanel.setAliasURI(aliasURI);
    }

    public AliasReferenceView(FormatUtil features) {
        HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        aliasDetailPanel.setFeatures(features);
    }


}