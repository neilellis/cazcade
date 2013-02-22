/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class TopBar extends Composite {
    interface TopBarUiBinder extends UiBinder<HTMLPanel, TopBar> {}

    private static TopBarUiBinder ourUiBinder = GWT.create(TopBarUiBinder.class);
    private final LSDBaseEntity    alias;
    @UiField      FormElement      login;
    @UiField      UListElement     userDetails;
    @UiField      AnchorElement    usernameLink;
    @UiField      FormElement      logout;
    @UiField      UserProfileImage userIcon;
    @UiField      AnchorElement    historyLink;
    @UiField      AnchorElement    yourBoardsLink;

    public TopBar() {
        alias = UserUtil.getCurrentAlias();
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        if (UserUtil.isAnonymousOrLoggedOut()) {
            logout.removeFromParent();
            userDetails.removeFromParent();
            historyLink.removeFromParent();
            yourBoardsLink.removeFromParent();
        } else {
            login.removeFromParent();
            usernameLink.setInnerText(alias.getAttribute(LSDAttribute.FULL_NAME));
            usernameLink.setHref("/~" + alias.getNameOrId());
            if (alias.hasAttribute(LSDAttribute.IMAGE_URL)) {
                userIcon.setUrl(alias.getAttribute(LSDAttribute.IMAGE_URL));
                userIcon.setAliasUri(alias.getURI());
            }
        }

    }


}