/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class StartupUtil {
    public static void showCachedVersion() {
        //        RootPanel.get("cache-panel").getElement().getStyle().setOpacity(1.0);
        //        RootPanel.get("loading-panel").getElement().getStyle().setOpacity(0.0);
    }

    public static void showLiveVersion(@Nonnull final Element panel) {
        final RootPanel loadingPanel = RootPanel.get("loading-panel");
        if (loadingPanel != null) {
            loadingPanel.getElement().getStyle().setOpacity(0.0);
            //            RootPanel.get("loading-panel").getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        final RootPanel cachePanel = RootPanel.get("cache-panel");
        if (cachePanel != null) {
            cachePanel.getElement().getStyle().setOpacity(0.05);
            panel.getStyle().setOpacity(0.0);
            panel.getStyle().setVisibility(Style.Visibility.VISIBLE);
            new Timer() {
                @Override public void run() {
                    panel.getStyle().setOpacity(1.0);
                }
            }.schedule(300);
            new Timer() {
                @Override public void run() {
                    cachePanel.getElement().removeFromParent();
                }
            }.schedule(30000);
        }
    }
}
