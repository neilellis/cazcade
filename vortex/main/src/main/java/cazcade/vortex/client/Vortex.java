/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.client;

import cazcade.liquid.api.SessionIdentifier;
import cazcade.vortex.client.widgets.VortexSplitPanel;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.comms.datastore.client.GWTDataStore;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class Vortex implements EntryPoint {

    private final VortexInjector injector = GWT.create(VortexInjector.class);
    private Panel mainPanelParent;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        if (Window.Location.getParameterMap().containsKey("debug")) {
            final HTMLPanel logPanel = new HTMLPanel("log");
            DOM.setStyleAttribute(logPanel.getElement(), "background", "white");
            final ScrollPanel scrollPanel = new ScrollPanel();
            scrollPanel.setWidth("100%");
            scrollPanel.setHeight("100px");
            scrollPanel.add(logPanel);
            final DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
            panel.setWidth("100%");
            panel.setHeight("100%");
            panel.addSouth(scrollPanel, 100);
            RootLayoutPanel.get().add(panel);
            ClientLog.logWidget = logPanel.getElement();
            mainPanelParent = panel;
        } else {
            mainPanelParent = RootLayoutPanel.get();

        }
        DataStoreService.App.getInstance().login("neil", "neil", new AsyncCallback<SessionIdentifier>() {
            public void onFailure(@Nonnull final Throwable caught) {
                ClientLog.log(caught.getMessage(), caught);
            }

            public void onSuccess(@Nullable final SessionIdentifier result) {
                ClientLog.log("Logged in.");
                if (result == null) {
                    ClientLog.log("Could not log in.");
                } else {
                    new GWTDataStore(result, new Runnable() {
                        @Override
                        public void run() {
                            final VortexSplitPanel vortexSplitPanel = new VortexSplitPanel(result);
                            vortexSplitPanel.setHeight("100%");
                            vortexSplitPanel.setWidth("100%");
                            mainPanelParent.add(vortexSplitPanel);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {

                        }
                    }
                    );
                }
            }
        });

    }


}
