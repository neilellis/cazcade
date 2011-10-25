package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class HistoryManager {



    Map<String, HistoryAwareComposite> compositeMap = new HashMap<String, HistoryAwareComposite>();

    public HistoryManager(final String mainPanelId) {
        if (RootPanel.get(mainPanelId) != null) {
            com.google.gwt.user.client.History.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                    String tokenFirstPart;
                    String newToken = stringValueChangeEvent.getValue();
                    if (newToken.contains(";")) {
                        newToken = newToken.substring(0,newToken.lastIndexOf(';'));
                    }

                    if (newToken.contains(":")) {
                        tokenFirstPart = newToken.split(":")[0];
                    } else {
                        tokenFirstPart = "default";
                    }
                    final HistoryAwareComposite composite = compositeMap.get(tokenFirstPart);
                    if (composite != null) {
                        final Widget currentWidget = RootPanel.get(mainPanelId).iterator().next();
                        if (currentWidget != null) {
                            currentWidget.removeFromParent();
                        }
                        composite.addStyleName("main-content-panel");
                        String localToken;
                        if (newToken.contains(":")) {
                            localToken = newToken.substring(tokenFirstPart.length() + 1);
                        } else {
                            localToken = newToken;
                        }
                        //darn jsessionid things
                        composite.onLocalHistoryTokenChanged(localToken);

                        RootPanel.get(mainPanelId).add(composite);
                    }
                }
            });
        }
    }

    public static void changeHistoryTokenWithoutCreatingHistoryForPrevious(String token) {
        Window.Location.replace(Window.Location.getHref().substring(0, Window.Location.getHref().indexOf("#")) + "#" + token);
    }

    public void registerTopLevelComposite(String token, HistoryAwareComposite composite) {
        compositeMap.put(token, composite);
        composite.setHistoryManager(this);
        composite.setHistoryToken(token);
    }


    public void addHistory(String historyToken, String localHistory) {
        History.newItem(historyToken + ":" + localHistory);
    }
}
