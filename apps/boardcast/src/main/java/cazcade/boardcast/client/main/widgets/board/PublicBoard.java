/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.boardcast.client.main.widgets.AddChatBox;
import cazcade.boardcast.client.main.widgets.AddCommentBox;
import cazcade.boardcast.client.main.widgets.BoardMenuBar;
import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.*;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.*;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import cazcade.vortex.widgets.client.profile.ProfileBoardHeader;
import cazcade.vortex.widgets.client.stream.ChatStreamPanel;
import cazcade.vortex.widgets.client.stream.CommentPanel;
import cazcade.vortex.widgets.client.stream.NotificationPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.Permission.*;
import static cazcade.liquid.api.PermissionScope.WORLD_SCOPE;
import static cazcade.liquid.api.RequestType.R_CHANGE_PERMISSION;
import static cazcade.liquid.api.RequestType.R_UPDATE_POOL;
import static cazcade.liquid.api.lsd.Dictionary.*;
import static cazcade.liquid.api.lsd.Types.*;
import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PublicBoard extends EntityBackedFormPanel {
    interface NewBoardUiBinder extends UiBinder<HTMLPanel, PublicBoard> {}

    public static final  String                   CORKBOARD   = "/_static/_background/misc/corkboard.jpg";
    private static final NewBoardUiBinder         ourUiBinder = GWT.create(NewBoardUiBinder.class);
    @Nonnull
    private final        BusService               bus         = Bus.get();
    @Nonnull
    private final        VortexThreadSafeExecutor executor    = new VortexThreadSafeExecutor();
    @UiField CommentPanel           comments;
    @UiField AddCommentBox          addCommentBox;
    @UiField PoolContentArea        content;
    @UiField BoardMenuBar           menuBar;
    //    @UiField
    //    DivElement boardLockedIcon;
    //    @UiField
    //    HTMLPanel shareThisHolder;
    @UiField AliasDetailFlowPanel   ownerDetailPanel;
    @UiField SpanElement            authorFullname;
    @UiField SpanElement            publishDate;
    @UiField PublicBoardHeader      publicBoardHeader;
    @UiField ProfileBoardHeader     profileBoardHeader;
    @UiField NotificationPanel      notificationPanel;
    @UiField DivElement             footer;
    @UiField IFrameElement          tweetButton;
    @UiField SpanElement            visibilityDescription;
    @UiField DivElement             containerDiv;
    @UiField AddChatBox             addChatBox;
    @UiField ChatStreamPanel        stream;
    private  long                   updatePoolListener;
    private  ChangeBackgroundDialog changeBackgroundDialog;
    private  boolean                inited;
    private  LURI                   uri;
    private  LURI                   previousUri;
    private  long                   changePermissionListener;
    //    private Element sharethisElement;
    private  boolean                chatMode;

    public PublicBoard() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                sizeNotificationPanel();
            }
        });
        bind(getChangeBackgroundDialog(), BACKGROUND_URL);
        getWidget().getElement().getStyle().setOpacity(0.0);
    }

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
        navigate(token);
    }

    /*
    private void addUserInfo() {
        final TransferEntity alias = User.currentAlias();
        if (alias == null || User.anon()) {
            userImage.setVisible(false);
            //TODO: Change this to 'Login' when bug found
            userFullName.setText("");
            userFullName.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.Location.assign("login.html");
                }
            });
//            accountBar.setVisible(false);
        } else {
            userImage.setUrl(alias.$(Attribute.ICON_URL));
            userFullName.setText(alias.$(Attribute.FULL_NAME));
            userFullName.sinkEvents(Event.ONCLICK);
//            userImage.sinkEvents(Event.ONCLICK);
//            userImage.addHandler(new ChangeProfileClickHandler(alias), ClickEvent.type());
            userFullName.addClickHandler(new ChangeProfileClickHandler(alias));
            userImage.addClickHandler(new ChangeProfileClickHandler(alias));

        }
    }
    */

    @Override public void beforeInactive() {
        super.beforeInactive();
        clear();
    }

    public void navigate(@Nullable final String value) {
        Track.getInstance().trackEvent("Board", value);
        if (value == null || value.startsWith(".") || value.startsWith("_") || value.isEmpty()) {
            Window.alert("Invalid board name " + value);
            return;
        }
        if (uri != null && uri.board().safe().equalsIgnoreCase(value)) {
            return;
        }
        previousUri = uri;
        uri = new LURI(BoardURL.from(value));
        if (isAttached()) {
            $.async(new Runnable() {
                @Override public void run() {
                    refresh();
                }
            });

        }
    }

    private void refresh() {
        ClientLog.log(ClientLog.Type.HISTORY, "PublicBoard.refresh()");
        clear();

        if (changePermissionListener != 0) { bus.remove(changePermissionListener); }

        changePermissionListener = bus.listenForSuccess(uri, R_CHANGE_PERMISSION, new BusListener() {
            @Override
            public void handle(final LiquidMessage message) {
                Window.alert("The access rights have just changed for this board, please refresh the page in your browser.");
            }
        });

        if (updatePoolListener != 0) { bus.remove(updatePoolListener); }

        updatePoolListener = bus.listenForSuccess(uri, R_UPDATE_POOL, new BusListener() {
            @Override
            public void handle(final LiquidMessage response) {
                update((LiquidRequest) response);
            }
        });

        if (previousUri == null || !previousUri.equals(uri)) { content.clear(); }

        Request.visit(T_BOARD, uri, previousUri, uri.board().listedConvention(), new Callback<VisitPoolRequest>() {
                    @Override public void handle(VisitPoolRequest message) throws Exception {
                        final TransferEntity resp = message.response();
                        if (resp.canBe(T_RESOURCE_NOT_FOUND)) {
                            Window.alert("Could not find the board.");
                            $.navigate(previousUri != null ? previousUri.board().toString() : "welcome");
                        } else if (resp.canBe(T_POOL)) {
                            $(resp.$());
                        } else {
                            Window.alert(resp.$(TITLE));
                        }
                    }
                }, new Callback<VisitPoolRequest>() {
                    @Override public void handle(VisitPoolRequest message) throws Exception {
                        Window.alert(message.response().type().canBe(T_RESOURCE_NOT_FOUND)
                                     ? (User.anon()
                                        ? "Please login first."
                                        : "You don't have permission")
                                     : message.response().$(TITLE));
                        ClientLog.log(message);

                    }
                }, !User.anon()
                     );

    }

    private void update(@Nonnull final LiquidRequest response) {
        $(response.response().$());
    }

    @Override protected boolean isSaveOnExit() {
        return false;
    }

    @Nonnull @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                Request.updatePool(field.getEntityDiff(), new Callback<UpdatePoolRequest>() {
                            @Override public void handle(UpdatePoolRequest message) throws Exception {
                                $(message.response().$());
                            }
                        }, new Callback<UpdatePoolRequest>() {
                            @Override public void handle(UpdatePoolRequest message) throws Exception {
                                field.setErrorMessage(message.response().$(DESCRIPTION));
                            }
                        }
                                  );
            }
        };
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        sizeNotificationPanel();
    }

    private void sizeNotificationPanel() {
        notificationPanel.getElement().getStyle().setLeft(content.getAbsoluteLeft(), Style.Unit.PX);
        notificationPanel.getElement()
                         .getStyle()
                         .setRight(Window.getClientWidth() - (content.getAbsoluteLeft() + content.getOffsetWidth()), Style.Unit.PX);
    }

    private void clear() {
        publicBoardHeader.clear();
        profileBoardHeader.clear();
        ownerDetailPanel.clear();
        authorFullname.setInnerText("");
        publishDate.setInnerText("");
        notificationPanel.clear();
        stream.clear();
    }

    @Override
    protected void onChange(@Nonnull final Entity entity) {
        addStyleName("readonly");
        addStyleName("loading");

        final Entity owner = $().child(A_OWNER, true);

        if (entity.$bool(MODIFIABLE)) {
            addStyleName("modifiable-board");
        } else {
            removeStyleName("modifiable-board");
        }

        if ($().uri().board().profile()) {
            publicBoardHeader.getElement().getStyle().setDisplay(Style.Display.NONE);
            profileBoardHeader.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            ownerDetailPanel.setVisible(false);
            footer.getStyle().setDisplay(Style.Display.NONE);
            profileBoardHeader.setAliasURI(owner.uri());
            tweetButton.setSrc("http://platform.twitter.com/widgets/tweet_button.html?url=" + encode("http://boardcast.it/" + entity
                    .uri()
                    .board()
                    .safe()) +
                               "&text=" + encode("Check out this profile on Boardcast #bc") + "&count=horizontal");
        } else {
            publicBoardHeader.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            profileBoardHeader.getElement().getStyle().setDisplay(Style.Display.NONE);
            publicBoardHeader.$($());
            footer.getStyle().setVisibility(Style.Visibility.VISIBLE);
            ownerDetailPanel.setAliasURI(owner.uri());
            ownerDetailPanel.setVisible(!User.isAlias(owner.uri()));
            tweetButton.setSrc("http://platform.twitter.com/widgets/tweet_button.html?url=" + encode("http://boardcast.it/" + entity
                    .uri()
                    .board()
                    .safe()) + "&text=" + encode("Check out " +
                                                 entity.default$(TITLE, "this board") +
                                                 " on Boardcast #bc") + "&count=horizontal&hashtags=bc&via=boardcast_it");
            footer.getStyle().setDisplay(Style.Display.BLOCK);
        }
        authorFullname.setInnerText(owner.$(FULL_NAME));
        publishDate.setInnerText($().published().toString());
        final String shortUrl = $().uri().board().safe();
        String tagText = "";
        if (!shortUrl.startsWith("-")) {
            tagText = " The hashtag for this board is #" +
                      shortUrl +
                      ", the tag can be placed in comments and text and will link " +
                      "back to this board.";
        }
        visibilityDescription.setInnerText(buildVisibilityDescription() + " There have been " + $().$(VISITS_METRIC) +
                                           " visits including " +
                                           $().$(REGISTERED_VISITORS_METRIC) +
                                           " registered users and " +
                                           $().default$(COMMENT_COUNT, "no") +
                                           " comments left." + tagText);

        Window.setTitle("Boardcast : " + $().$(TITLE));

        //                    final String imageUrl = "/_website-snapshot?url="
        //                                            + URL.encode("http://boardcast.it/_snapshot-" + shortUrl + "?bid=" + System.currentTimeMillis())
        //                                            + "&size=LARGE&width=150&height=200&delayAsync=60";

        if (previousUri == null || !previousUri.equals(uri)) {
            $.async(new Runnable() {
                @Override public void run() {
                    content.init($(), executor);
                    menuBar.init(PublicBoard.this, $(), $().$bool(MODIFIABLE), getChangeBackgroundDialog());
                    if ($().$bool(MODIFIABLE)) {
                        removeStyleName("readonly");
                    }
                    removeStyleName("loading");
                    StartupUtil.showLiveVersion(getWidget().getElement());
                }
            });


            $.async(new Runnable() {
                @Override public void run() {
                    comments.clear();
                    comments.init(uri);
                    if (Config.alpha()) {
                        notificationPanel.init(uri);
                    }
                    addCommentBox.init(uri);
                }
            });
        }

    }

    @Nonnull
    private String buildVisibilityDescription() {
        if (entity == null) {
            return "";
        }
        return entity.$bool(LISTED)
               ? "It is a listed board which is " + (entity.allowed(WORLD_SCOPE, P_VIEW)
                                                     ? "visible to all"
                                                       + (entity.allowed(WORLD_SCOPE, P_EDIT)
                                                          ? " and editable by all."
                                                          : entity.allowed(WORLD_SCOPE, P_MODIFY)
                                                            ? " and modifiable by all."
                                                            : ". ")
                                                     : "currently only visible to the creator.")
               : "It is an unlisted board which is " + (entity.allowed(WORLD_SCOPE, P_VIEW) ?
                                                        "visible to those who know the URL "
                                                        + (entity.allowed(WORLD_SCOPE, P_EDIT)
                                                           ? " and editable by them. "
                                                           : entity.allowed(WORLD_SCOPE, P_EDIT)
                                                             ? " and modifiable by them. "
                                                             : ". ") : "visible only to the creator.");
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (!inited) {
            init();
            inited = true;
        }
    }

    private void init() {

        addCommentBox.sinkEvents(Event.MOUSEEVENTS);
        if (uri != null) {
            $.async(new Runnable() {
                @Override public void run() {
                    refresh();
                }
            });

        }
    }

    public ChangeBackgroundDialog getChangeBackgroundDialog() {
        if (changeBackgroundDialog == null) {
            changeBackgroundDialog = new ChangeBackgroundDialog();
        }
        return changeBackgroundDialog;
    }

    public void toggleChat() {
        chatMode = getWidget().getElement().getAttribute("class").contains("chat");
        if (chatMode) {
            getWidget().removeStyleName("chat");
        } else {
            getWidget().addStyleName("chat");
            addChatBox.init(uri);
            stream.init(uri);
        }
        chatMode = !chatMode;
    }

}