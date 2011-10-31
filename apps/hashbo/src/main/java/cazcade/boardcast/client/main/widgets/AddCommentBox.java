package cazcade.boardcast.client.main.widgets;

import cazcade.boardcast.client.main.widgets.login.TwitterLoginBox;
import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.widgets.client.stream.CommentBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class AddCommentBox extends Composite {

    public void init(LiquidURI poolURI) {
        addCommentBox.init(poolURI);
        loginLink.setHref("login.html?userlogin#" + new LiquidBoardURL(poolURI));
        if (UserUtil.isAnonymousOrLoggedOut()) {
            loginPanel.setVisible(true);
            addCommentBox.setVisible(false);
        } else {
            loginPanel.setVisible(false);
            addCommentBox.setVisible(true);
        }
    }

    interface LoginOrCommentBoxUiBinder extends UiBinder<HTMLPanel, AddCommentBox> {
    }

    private static LoginOrCommentBoxUiBinder ourUiBinder = GWT.create(LoginOrCommentBoxUiBinder.class);
    @UiField
    CommentBox addCommentBox;
    @UiField
    HTMLPanel loginPanel;
    @UiField
    AnchorElement loginLink;
    @UiField
    TwitterLoginBox twitterLoginBox;

    public AddCommentBox() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}