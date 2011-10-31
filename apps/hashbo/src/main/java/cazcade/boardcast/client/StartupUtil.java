package cazcade.boardcast.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author neilellis@cazcade.com
 */
public class StartupUtil {
    public static void showCachedVersion() {
//        RootPanel.get("cache-panel").getElement().getStyle().setOpacity(1.0);
//        RootPanel.get("loading-panel").getElement().getStyle().setOpacity(0.0);
    }

    public static void showLiveVersion(Element panel) {
        if (RootPanel.get("loading-panel") != null) {
            RootPanel.get("loading-panel").getElement().getStyle().setOpacity(0.0);
            RootPanel.get("loading-panel").getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        if (RootPanel.get("cache-panel") != null) {
            RootPanel.get("cache-panel").getElement().getStyle().setOpacity(0.0);
            RootPanel.get("cache-panel").getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            panel.getStyle().setOpacity(1.0);
            panel.getStyle().setVisibility(Style.Visibility.VISIBLE);
            RootPanel.get("cache-panel").getElement().removeFromParent();
        }
    }
}
