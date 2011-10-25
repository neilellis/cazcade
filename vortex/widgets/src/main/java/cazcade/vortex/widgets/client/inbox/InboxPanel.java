package cazcade.vortex.widgets.client.inbox;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.widgets.client.panels.list.ScrollableList;
import cazcade.vortex.widgets.client.panels.list.dm.DirectMessageListEntryPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class InboxPanel extends Composite {
    private FormatUtil features;

    public void setFeatures(FormatUtil features) {
        if (this.features == null) {
            init();
        }
        this.features = features;
    }

    interface InboxPanelUiBinder extends UiBinder<HTMLPanel, InboxPanel> {
    }

    private static InboxPanelUiBinder ourUiBinder = GWT.create(InboxPanelUiBinder.class);

    @UiField
    ScrollableList list;

    public InboxPanel() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }

    public void init() {
        BusFactory.getInstance().send(new RetrievePoolRequest(UserUtil.getInboxURI(), true, false), new AbstractResponseCallback<RetrievePoolRequest>() {
            @Override
            public void onSuccess(RetrievePoolRequest request, RetrievePoolRequest response) {
                List<LSDEntity> messages = response.getResponse().getSubEntities(LSDAttribute.CHILD);
                for (LSDEntity message : messages) {
                    list.addEntry(new DirectMessageListEntryPanel(message, features));
                }
            }
        });
        BusFactory.getInstance().listenForURIAndSuccessfulRequestType(UserUtil.getCurrentAlias().getURI(), LiquidRequestType.SEND, new BusListener<SendRequest>() {
            @Override
            public void handle(SendRequest request) {
                list.addEntry(new DirectMessageListEntryPanel(request.getResponse(), features));
            }
        });
    }


}