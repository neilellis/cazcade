/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.request.FollowRequest;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.vortex.bus.client.*;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.Config;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.dm.DirectMessagePanel;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.RequestType.R_FOLLOW;
import static cazcade.liquid.api.RequestType.R_UPDATE_ALIAS;
import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractAliasDetailPanel extends EntityBackedFormPanel {
    private static final boolean DM_SUPPORTED = false;
    @UiField UserProfileImage    userImage;
    @UiField VortexEditableLabel userShortName;
    @UiField VortexEditableLabel userFullName;
    @UiField VortexEditableLabel description;
    @UiField AnchorElement       profileLink;
    //    @UiField
    //    AnchorElement publicLink;
    @UiField Label               followersLabel;
    @UiField Label               followingLabel;
    @UiField Label               followButton;
    @UiField Label               roleFullName;
    @UiField Label               dmButton;
    @UiField DirectMessagePanel  directMessagePanel;
    @UiField HTMLPanel           detailPanel;
    private  boolean             following;
    private  HandlerRegistration followHandler;
    private  HandlerRegistration dmHandler;
    private  long                followListenId;
    private  long                updateAliasListenId;
    private  LURI                aliasURI;

    protected void addBindings() {
        bind(userShortName, NAME);
        bind(userFullName, FULL_NAME);
        bind(description, DESCRIPTION);
        bind(userImage, IMAGE_URL);
    }

    @Override protected boolean isSaveOnExit() {
        return false;
    }

    @Nonnull
    protected String getReferenceDataPrefix() {
        return "profile";
    }

    @Nonnull
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {

                Bus.get().send(new UpdateAliasRequest(field.getEntityDiff()), new AbstractMessageCallback<UpdateAliasRequest>() {
                    @Override
                    public void onSuccess(final UpdateAliasRequest original, @Nonnull final UpdateAliasRequest message) {
                        $(message.response().$());
                        getWidget().getElement().getStyle().setOpacity(1.0);

                    }

                    @Override
                    public void onFailure(final UpdateAliasRequest original, @Nonnull final UpdateAliasRequest message) {
                        field.setErrorMessage(message.response().$(DESCRIPTION));
                    }


                });
            }
        };
    }

    public void setAliasURI(@Nonnull final LURI aliasURI) {
        this.aliasURI = aliasURI;


        final boolean isMe = User.currentAlias().uri().equals(aliasURI);

        if (!isMe) {
            userShortName.addClickHandler(new UsernameClickHandler());
            userShortName.sinkEvents(Event.MOUSEEVENTS);
            userFullName.addClickHandler(new UsernameClickHandler());
            userFullName.sinkEvents(Event.MOUSEEVENTS);
        }

        if (User.anon() || isMe || !Config.alpha()) {
            followButton.addStyleName("invisible");
            dmButton.addStyleName("invisible");
        } else {
            initDMAndFollow(aliasURI);
        }


        if (followListenId != 0) {
            Bus.get().remove(followListenId);
        }
        followListenId = Bus.get().listenForSuccess(aliasURI, R_FOLLOW, new BusListener<FollowRequest>() {
            @Override
            public void handle(@Nonnull final FollowRequest response) {
                    $(response.response().$());
            }
        });
        if (updateAliasListenId != 0) {
            Bus.get().remove(updateAliasListenId);
        }
        updateAliasListenId = Bus.get().listenForSuccess(aliasURI, R_UPDATE_ALIAS, new BusListener<UpdateAliasRequest>() {
            @Override
            public void handle(@Nonnull final UpdateAliasRequest response) {
                $(response.response().$());
            }
        });

        Bus.get().send(new RetrieveAliasRequest(aliasURI), new Callback<RetrieveAliasRequest>() {
            @Override
            public void handle( @Nonnull final RetrieveAliasRequest message) {
                $(message.response().$());

            }
        });


    }

    private void initDMAndFollow(@Nonnull final LURI aliasURI) {
        if (followHandler != null) {
            followHandler.removeHandler();
        }
        followHandler = followButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                Bus.get().dispatch(new FollowRequest(aliasURI, !following));
            }
        });
        if (dmHandler != null) {
            dmHandler.removeHandler();
        }
        directMessagePanel.setOnFinish(new Runnable() {
            @Override
            public void run() {
                WidgetUtil.swap(directMessagePanel, detailPanel);
            }
        });
        dmHandler = dmButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {

                if (Config.alpha()) {
                    directMessagePanel.setRecipient(aliasURI.sub().sub().asString());
                    WidgetUtil.swap(detailPanel, directMessagePanel);
                    directMessagePanel.setVisible(true);
                    directMessagePanel.start();
                } else {
                    Window.alert("Feature coming very soon.");
                }
            }
        });
    }

    public void onChange(@Nullable final Entity entity) {
        if (entity == null) {
            return;
        }
        getWidget().setVisible(true);
        if (entity.has(ROLE_TITLE)) {
            roleFullName.setText(entity.$(ROLE_TITLE));
        } else {
            roleFullName.setText("Mysterious Being");
        }
        if (entity.has(NAME)) {
            final String username = entity.$(NAME);
            profileLink.setHref("#@" + username);
        }
        //        publicLink.setHref("#public@" + username);
        followersLabel.setText(entity.default$(FOLLOWERS_COUNT, "no") + " followers");
        followingLabel.setText(entity.default$(FOLLOWS_ALIAS_COUNT, "no") + " follows");
        following = entity.default$bool(FOLLOWING, false);
        followButton.setText(following ? "Unfollow" : "Follow");
        WidgetUtil.showGracefully(this, true);

    }

    public void clear() {
        userImage.clear();
        userShortName.clear();
        userFullName.clear();
        description.clear();
        profileLink.setName("");
        followersLabel.setText("");
        followingLabel.setText("");
        directMessagePanel.clear();
    }

    private class UsernameClickHandler implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            History.newItem("#@" + userShortName.getValue());
        }
    }
}
