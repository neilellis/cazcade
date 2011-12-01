package cazcade.vortex.widgets.client.panels.list.dm;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.FormatUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class DirectMessageStreamEntryPanel extends DirectMessageListEntryPanel {
    interface DirectMessageStreamEntryPanelUiBinder extends UiBinder<HTMLPanel, DirectMessageStreamEntryPanel> {
    }

    private static final DirectMessageStreamEntryPanelUiBinder ourUiBinder = GWT.create(DirectMessageStreamEntryPanelUiBinder.class);

    public DirectMessageStreamEntryPanel(@Nonnull final LSDEntity streamEntry, @Nonnull final FormatUtil features) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        init(streamEntry, features);
    }
}