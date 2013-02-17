/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AddCommentBox extends Composite {
    interface AddCommentBoxUiBinder extends UiBinder<HTMLPanel, AddCommentBox> {}

    private static final AddCommentBoxUiBinder ourUiBinder = GWT.create(AddCommentBoxUiBinder.class);

    @UiField CommentBox      addCommentBox;
    @UiField HTMLPanel       loginPanel;
    @UiField AnchorElement   loginLink;
    @UiField TwitterLoginBox twitterLoginBox;

    public AddCommentBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void init(@Nonnull final LiquidURI poolURI) {
        addCommentBox.init(poolURI);
        loginLink.setHref("/" + new LiquidBoardURL(poolURI).asUrlSafe() + "?forceLogin=true");
        if (UserUtil.isAnonymousOrLoggedOut()) {
            loginPanel.setVisible(true);
            addCommentBox.setVisible(false);
        } else {
            loginPanel.setVisible(false);
            addCommentBox.setVisible(true);
        }
    }
}