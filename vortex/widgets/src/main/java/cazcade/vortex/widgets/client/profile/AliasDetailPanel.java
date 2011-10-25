package cazcade.vortex.widgets.client.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class AliasDetailPanel extends AbstractAliasDetailPanel {


    interface AliasDetailPanelUiBinder extends UiBinder<HTMLPanel, AliasDetailPanel> {
    }

    private static AliasDetailPanelUiBinder ourUiBinder = GWT.create(AliasDetailPanelUiBinder.class);


    public AliasDetailPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));


    }
}