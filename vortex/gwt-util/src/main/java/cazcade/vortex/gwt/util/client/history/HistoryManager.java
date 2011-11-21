package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles a slightly more complex world view than the simple
 * History.java provided by GWT.
 *
 * @author neilellis@cazcade.com
 */
public class HistoryManager {


    Map<String, HistoryAwareFactory> compositeMap = new HashMap<String, HistoryAwareFactory>();
    private String mainPanelId;

    public HistoryManager(final String mainPanelId) {
        this.mainPanelId = mainPanelId;
        if (RootPanel.get(mainPanelId) != null) {
            com.google.gwt.user.client.History.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                    String newToken = stringValueChangeEvent.getValue();
                    handleTokenChange(newToken);
                }
            });
        }
    }

    private void handleTokenChange(String newToken) {
        String tokenFirstPart;
        //grrr Jsession id nonsense
        if (newToken.contains(";")) {
            newToken = newToken.substring(0, newToken.lastIndexOf(';'));
        }
        if (newToken.contains("?")) {
            newToken = newToken.substring(0, newToken.lastIndexOf('?'));
        }
        final String localToken;

        if (newToken.contains(":")) {
            final String[] strings = newToken.split(":");
            tokenFirstPart = strings[0];
            localToken = strings[1];
        } else if (newToken.startsWith("_")) {
            final String[] strings = newToken.substring(1).split("-");
            tokenFirstPart = strings[0];
            localToken = strings[1];
        } else {
            tokenFirstPart = "default";
            localToken = newToken;
        }
        compositeMap.get(tokenFirstPart).withInstance(
                new HistoryAwareFactoryCallback() {
                    @Override
                    public void withInstance(HistoryAware composite) {
                        if (composite != null) {
                            final Widget currentWidget = RootPanel.get(mainPanelId).iterator().next();
                            if (currentWidget != null) {
                                currentWidget.removeFromParent();
                            }
                            if (composite.addToRootPanel()) {
                                composite.asWidget().addStyleName("main-content-panel");
                                RootPanel.get(mainPanelId).add(composite);
                            }
                            composite.onLocalHistoryTokenChanged(localToken);

                        }
                    }
                }
        );

    }

    public void registerTopLevelComposite(String token, HistoryAwareFactory composite) {
        compositeMap.put(token, composite);
        composite.setHistoryManager(this);
        composite.setHistoryToken(token);
    }


    public void addHistory(String historyToken, String localHistory) {
        navigate(historyToken + ":" + localHistory);
    }

    public static void navigate(String url) {
        if (isPushStateSupported()) {
            History.newItem(url);
        } else {
            Window.Location.assign("/" + url);
        }
    }

    private static final native boolean isPushStateSupported()/*-{
        return typeof($wnd.history.pushState) == "function" && !$wnd.testNoPushState;
    }-*/;

    public void fireCurrentHistoryState() {
        if (isPushStateSupported()) {
            History.fireCurrentHistoryState();
        } else {
            handleTokenChange(Window.Location.getPath().substring(1));
        }
    }
}
