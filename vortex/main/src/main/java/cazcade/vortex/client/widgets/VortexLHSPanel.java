package cazcade.vortex.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author neilellis@cazcade.com
 */
public class VortexLHSPanel extends Composite {
    interface VortexLHSPanelUiBinder extends UiBinder<SimplePanel, VortexLHSPanel> {
    }

    private static final VortexLHSPanelUiBinder ourUiBinder = GWT.create(VortexLHSPanelUiBinder.class);

    public VortexLHSPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}