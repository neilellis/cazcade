package cazcade.vortex.widgets.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class ActivityPanel extends Composite {
    interface InboxPanelUiBinder extends UiBinder<HTMLPanel, ActivityPanel> {
    }

    private static final InboxPanelUiBinder ourUiBinder = GWT.create(InboxPanelUiBinder.class);

    public ActivityPanel() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);

    }
}