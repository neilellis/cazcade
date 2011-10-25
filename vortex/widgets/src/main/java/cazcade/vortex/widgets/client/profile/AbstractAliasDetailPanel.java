package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.FollowRequest;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.vortex.bus.client.AbstractBusListener;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.dm.DirectMessagePanel;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.form.fields.VortexFormField;
import cazcade.vortex.widgets.client.image.EditableImage;
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
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractAliasDetailPanel extends EntityBackedFormPanel {
    private FormatUtil features;
    private boolean following;
    private HandlerRegistration followHandler;
    private HandlerRegistration dmHandler;
    private long followListenId;
    private long updateAliasListenId;
    private LiquidURI aliasURI;
    @UiField
    UserProfileImage userImage;
    @UiField
    VortexEditableLabel userShortName;
    @UiField
    VortexEditableLabel userFullName;
    @UiField
    VortexEditableLabel description;
    @UiField
    AnchorElement profileLink;
    //    @UiField
//    AnchorElement publicLink;
    @UiField
    Label followersLabel;
    @UiField
    Label followingLabel;
    @UiField
    Label followButton;
    @UiField
    Label roleFullName;
    @UiField
    Label dmButton;
    @UiField
    DirectMessagePanel directMessagePanel;
    @UiField
    HTMLPanel detailPanel;

    private static final boolean DM_SUPPORTED = false;



    public void bind(LSDEntity entity) {
        super.bind(entity);
        addBinding(userShortName, LSDAttribute.NAME);
        addBinding(userFullName, LSDAttribute.FULL_NAME);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(userImage, LSDAttribute.IMAGE_URL);
    }


    protected String getReferenceDataPrefix() {
        return "profile";
    }

    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {

                getBus().send(new UpdateAliasRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdateAliasRequest>() {
                    @Override
                    public void onSuccess(UpdateAliasRequest message, UpdateAliasRequest response) {
                        setEntity(response.getResponse());
                        getWidget().getElement().getStyle().setOpacity(1.0);

                    }

                    @Override
                    public void onFailure(UpdateAliasRequest message, UpdateAliasRequest response) {
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }


                });
            }
        };
    }


    public void setAliasURI(final LiquidURI aliasURI) {
        this.aliasURI = aliasURI;



        final boolean isMe = UserUtil.getCurrentAlias().getURI().equals(aliasURI);

        if(!isMe) {
            userShortName.addClickHandler(new UsernameClickHandler());
            userShortName.sinkEvents(Event.MOUSEEVENTS);
            userFullName.addClickHandler(new UsernameClickHandler());
            userFullName.sinkEvents(Event.MOUSEEVENTS);
        }

        if (UserUtil.isAnonymousOrLoggedOut() || isMe) {
            followButton.addStyleName("invisible");
            dmButton.addStyleName("invisible");
        } else {
            if (followHandler != null) {
                followHandler.removeHandler();
            }
            followHandler = followButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    BusFactory.getInstance().dispatch(new FollowRequest(aliasURI, !following));
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
                public void onClick(ClickEvent event) {

                    if (ClientApplicationConfiguration.isAlphaFeatures()) {
                        directMessagePanel.setRecipient(aliasURI.getSubURI().getSubURI().asString());
                        WidgetUtil.swap(detailPanel, directMessagePanel);
                        directMessagePanel.setVisible(true);
                        directMessagePanel.start();
                    } else {
                        Window.alert("Feature coming very soon.");
                    }
                }
            });
        }


        if (followListenId != 0) {
            BusFactory.getInstance().removeListener(followListenId);
        }
        followListenId = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(aliasURI, LiquidRequestType.FOLLOW, new AbstractBusListener<FollowRequest>() {
            @Override
            public void handle(FollowRequest response) {
                if (response.getUri().equals(aliasURI)) {
                    bind(response.getResponse());
                }
            }
        });
        if (updateAliasListenId != 0) {
            BusFactory.getInstance().removeListener(updateAliasListenId);
        }
        updateAliasListenId = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(aliasURI, LiquidRequestType.UPDATE_ALIAS, new AbstractBusListener<UpdateAliasRequest>() {
            @Override
            public void handle(UpdateAliasRequest response) {
                if (response.getUri().equals(aliasURI)) {
                    bind(response.getResponse());
                }
            }
        });

        BusFactory.getInstance().send(new RetrieveAliasRequest(aliasURI), new AbstractResponseCallback<RetrieveAliasRequest>() {
            @Override
            public void onSuccess(RetrieveAliasRequest message, RetrieveAliasRequest response) {
                bind(response.getResponse());

            }
        });


    }


    public void onChange(final LSDEntity entity) {
        if (entity == null) {
            return;
        }
        getWidget().setVisible(true);
        String username = entity.getAttribute(LSDAttribute.NAME);
        if (entity.hasAttribute(LSDAttribute.ROLE_TITLE)) {
            roleFullName.setText(entity.getAttribute(LSDAttribute.ROLE_TITLE));
        } else {
            roleFullName.setText("Newcomer");
        }
        profileLink.setHref("#@" + username);
//        publicLink.setHref("#public@" + username);
        followersLabel.setText(entity.getAttribute(LSDAttribute.FOLLOWERS_COUNT, "no") + " followers");
        followingLabel.setText(entity.getAttribute(LSDAttribute.FOLLOWS_ALIAS_COUNT, "no") + " follows");
        following = entity.getBooleanAttribute(LSDAttribute.FOLLOWING);
        followButton.setText(following ? "Unfollow" : "Follow");
        WidgetUtil.showGracefully(this, true);

    }

    public void setFeatures(FormatUtil features) {
        this.features = features;
    }

    private class UsernameClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            History.newItem("#@" + userShortName.getValue());
        }
    }
}
