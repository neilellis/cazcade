package cazcade.vortex.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author neilellis@cazcade.com
 */
public class DockPanel extends Composite {
    interface DockPanelUiBinder extends UiBinder<HorizontalPanel, DockPanel> {
    }

    private static final DockPanelUiBinder ourUiBinder = GWT.create(DockPanelUiBinder.class);

    public DockPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}