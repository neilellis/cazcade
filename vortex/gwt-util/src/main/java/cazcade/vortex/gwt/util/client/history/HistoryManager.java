package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles a slightly more complex world view than the simple
 * History.java provided by GWT.
 *
 * @author neilellis@cazcade.com
 */
public class HistoryManager {


    @Nonnull
    final Map<String, HistoryAwareFactory> compositeMap = new HashMap<String, HistoryAwareFactory>();
    private final String mainPanelId;

    public HistoryManager(final String mainPanelId) {
        this.mainPanelId = mainPanelId;
        if (RootPanel.get(mainPanelId) != null) {
            History.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(@Nonnull final ValueChangeEvent<String> stringValueChangeEvent) {
                    final String newToken = stringValueChangeEvent.getValue();
                    handleTokenChange(newToken);
                }
            });
        }
    }

    private void handleTokenChange(@Nonnull String newToken) {
        final String tokenFirstPart;
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
            final int dashPosition = newToken.indexOf('-');
            if (dashPosition <= 1) {
                tokenFirstPart = newToken.substring(1);
                localToken = "";
            } else {
                tokenFirstPart = newToken.substring(1, dashPosition);
                localToken = newToken.substring(dashPosition + 1);
            }
        } else {
            tokenFirstPart = "default";
            localToken = newToken;
        }
        final HistoryAwareFactory historyAwareFactory = compositeMap.get(tokenFirstPart);
        if (historyAwareFactory == null) {
            throw new IllegalArgumentException("Unrecognized history component " + tokenFirstPart);
        }
        historyAwareFactory.withInstance(
                new HistoryAwareFactoryCallback() {
                    @Override
                    public void withInstance(@Nullable final HistoryAware composite) {
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

    public void registerTopLevelComposite(final String token, @Nonnull final HistoryAwareFactory composite) {
        compositeMap.put(token, composite);
        composite.setHistoryManager(this);
        composite.setHistoryToken(token);
    }


    public void addHistory(final String historyToken, final String localHistory) {
        navigate(historyToken + ":" + localHistory);
    }

    public static void navigate(final String action, final String local) {
        navigate("_" + action + "-" + local);
    }

    public static void navigate(final String url) {
        if (isPushStateSupported()) {
            History.newItem(url);
        } else {
            Window.Location.assign("/" + url);
        }
    }

    private static native boolean isPushStateSupported()/*-{
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
