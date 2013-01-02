package cazcade.boardcast.client.main.widgets.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author neilellis@cazcade.com
 */
public class TwitterLoginPopup extends PopupPanel {
    interface TwitterLoginPopupUiBinder extends UiBinder<HTMLPanel, TwitterLoginPopup> {
    }

    private static final TwitterLoginPopupUiBinder ourUiBinder = GWT.create(TwitterLoginPopupUiBinder.class);

    public void showCenter() {
        Window.open("./_twitter/signin", "Sign In With Twitter", "width=700,height=512");
    }

    public TwitterLoginPopup() {
        super(true, true);
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        //    <g:PopupPanel glassEnabled="true" modal="true" width="700" height="512" ui:field="popup" >
        setGlassEnabled(true);
        add(rootElement);

    }

}