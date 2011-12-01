package cazcade.vortex.widgets.client.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class AliasDetailFlowPanel extends AbstractAliasDetailPanel {


    interface AliasDetailPanelUiBinder extends UiBinder<HTMLPanel, AliasDetailFlowPanel> {
    }

    private static final AliasDetailPanelUiBinder ourUiBinder = GWT.create(AliasDetailPanelUiBinder.class);


    public AliasDetailFlowPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));


    }
}