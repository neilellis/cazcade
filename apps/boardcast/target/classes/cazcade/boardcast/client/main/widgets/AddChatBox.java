package cazcade.boardcast.client.main.widgets;

import cazcade.boardcast.client.main.widgets.login.TwitterLoginBox;
import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.widgets.client.stream.ChatBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class AddChatBox extends Composite {
    private static final LoginOrCommentBoxUiBinder ourUiBinder = GWT.create(LoginOrCommentBoxUiBinder.class);
    @UiField
    ChatBox addChatBox;
    @UiField
    HTMLPanel loginPanel;
    @UiField
    AnchorElement loginLink;
    @UiField
    TwitterLoginBox twitterLoginBox;

    public AddChatBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void init(final LiquidURI poolURI) {
        addChatBox.init(poolURI);
        loginLink.setHref("login?loginNextUrl=" + URL.encode(Window.Location.getHref()));
        if (UserUtil.isAnonymousOrLoggedOut()) {
            loginPanel.setVisible(true);
            addChatBox.setVisible(false);
        }
        else {
            loginPanel.setVisible(false);
            addChatBox.setVisible(true);
        }
    }

    interface LoginOrCommentBoxUiBinder extends UiBinder<HTMLPanel, AddChatBox> {
    }
}