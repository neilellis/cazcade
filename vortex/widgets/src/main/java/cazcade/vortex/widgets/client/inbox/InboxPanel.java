/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.inbox;

import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
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

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class InboxPanel extends Composite {
    private FormatUtil features;

    public void setFeatures(final FormatUtil features) {
        if (this.features == null) {
            init();
        }
        this.features = features;
    }

    interface InboxPanelUiBinder extends UiBinder<HTMLPanel, InboxPanel> {}

    private static final InboxPanelUiBinder ourUiBinder = GWT.create(InboxPanelUiBinder.class);

    @UiField ScrollableList list;

    public InboxPanel() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }

    public void init() {
        BusFactory.get()
                  .send(new RetrievePoolRequest(UserUtil.getInboxURI(), true, false), new AbstractResponseCallback<RetrievePoolRequest>() {
                      @Override
                      public void onSuccess(final RetrievePoolRequest request, @Nonnull final RetrievePoolRequest response) {
                          final List<Entity> messages = response.response().children(Dictionary.CHILD_A);
                          for (final Entity message : messages) {
                              list.addEntry(new DirectMessageListEntryPanel(message));
                          }
                      }
                  });
        BusFactory.get().listenForSuccess(UserUtil.currentAlias().uri(), RequestType.SEND, new BusListener<SendRequest>() {
            @Override
            public void handle(@Nonnull final SendRequest request) {
                list.addEntry(new DirectMessageListEntryPanel(request.response()));
            }
        });
    }


}