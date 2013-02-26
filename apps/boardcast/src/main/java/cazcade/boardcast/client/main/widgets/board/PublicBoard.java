/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.boardcast.client.main.widgets.AddChatBox;
import cazcade.boardcast.client.main.widgets.AddCommentBox;
import cazcade.boardcast.client.main.widgets.BoardMenuBar;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.UserUtil;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.Permission.*;
import static cazcade.liquid.api.PermissionChangeType.MAKE_PUBLIC;
import static cazcade.liquid.api.PermissionChangeType.MAKE_PUBLIC_READONLY;
import static cazcade.liquid.api.PermissionScope.WORLD_SCOPE;
import static cazcade.liquid.api.RequestType.CHANGE_PERMISSION;
import static cazcade.liquid.api.RequestType.UPDATE_POOL;
import static cazcade.liquid.api.lsd.Dictionary.*;
import static cazcade.liquid.api.lsd.Types.*;
import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PublicBoard extends EntityBackedFormPanel {
    interface NewBoardUiBinder extends UiBinder<HTMLPanel, PublicBoard> {}

    public static final  String                   CORKBOARD          = "/_static/_background/misc/corkboard.jpg";
    private static final NewBoardUiBinder         ourUiBinder        = GWT.create(NewBoardUiBinder.class);
    @Nonnull
    private final        Bus                      bus                = BusFactory.get();
    @Nonnull
    private final        VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
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
    private  LiquidURI              uri;
    private  LiquidURI              previousUri;
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
        final TransferEntity alias = UserUtil.currentAlias();
        if (alias == null || UserUtil.anon()) {
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
        uri = new LiquidURI(BoardURL.from(value));
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
        if (changePermissionListener != 0) {
            bus.remove(changePermissionListener);
        }

        changePermissionListener = bus.listenForSuccess(uri, CHANGE_PERMISSION, new BusListener() {
            @Override
            public void handle(final LiquidMessage message) {
                Window.alert("The access rights have just changed for this board, please refresh the page in your browser.");
                //                refresh();
            }
        });

        if (updatePoolListener != 0) {
            bus.remove(updatePoolListener);
        }

        updatePoolListener = bus.listenForSuccess(uri, UPDATE_POOL, new BusListener() {
            @Override
            public void handle(final LiquidMessage response) {
                update((LiquidRequest) response);
            }
        });


        final boolean listed = uri.board().isListedByConvention();
        //start listed boards as public readonly, default is public writeable
        if (previousUri == null || !previousUri.equals(uri)) {
            content.clear();
        }

        bus.send(new VisitPoolRequest(T_BOARD, uri, previousUri, !UserUtil.anon(), listed, listed
                                                                                           ? MAKE_PUBLIC_READONLY
                                                                                           : null), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override
            public void onSuccess(final VisitPoolRequest message, @Nonnull final VisitPoolRequest response) {
                final TransferEntity resp = response.response();
                if (resp.canBe(T_RESOURCE_NOT_FOUND)) {
                    Window.alert("Could not find the board.");
                    if (previousUri != null) {
                        $.navigate(previousUri.board().toString());
                    }
                } else if (resp.canBe(T_POOL)) {
                    $(resp.$());
                } else {
                    Window.alert(resp.$(TITLE));
                }
            }

            @Override
            public void onFailure(final VisitPoolRequest message, @Nonnull final VisitPoolRequest resp) {
                if (resp.response().type().canBe(T_RESOURCE_NOT_FOUND)) {
                    if (UserUtil.anon()) {
                        Window.alert("Please login first.");
                    } else {
                        Window.alert("You don't have permission");
                    }
                } else {
                    super.onFailure(message, resp);
                }
            }


        });
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
                bus.send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        $(response.response().$());
                    }

                    @Override
                    public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        field.setErrorMessage(response.response().$(DESCRIPTION));
                    }
                });
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
        comments.clear();
        //        if (previousPoolURI == null || !previousPoolURI.equals(poolURI)) {
        //            contentArea.clear();
        //        }
        ownerDetailPanel.clear();
        authorFullname.setInnerText("");
        publishDate.setInnerText("");
        notificationPanel.clear();
        stream.clear();
    }

    //    private void configureShareThis(String imageUrl, String boardTitle, String board) {
    //        final NodeList<Element> spans = RootPanel.get("sharethisbar").getElement().getElementsByTagName("span");
    //        final int max = spans.getLength();
    //        for (int i = 0; i < max; i++) {
    //            final Element span = spans.getItem(i);
    //            if (span.has$("class") && "stButton".equalsIgnoreCase(span.$("class"))) {
    //                setShareThisDetails(board, "Take a look at the Boardcast board '" + boardTitle + "' ", "", imageUrl == null
    //                                                                                                           ? ""
    //                                                                                                           : imageUrl, span);
    //            }
    //
    //        }
    //    }

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

        if (!$().uri().board().profile()) {
            publicBoardHeader.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            profileBoardHeader.getElement().getStyle().setDisplay(Style.Display.NONE);
            publicBoardHeader.$($());
            footer.getStyle().setVisibility(Style.Visibility.VISIBLE);
            ownerDetailPanel.setAliasURI(owner.uri());
            ownerDetailPanel.setVisible(!UserUtil.isAlias(owner.uri()));
            tweetButton.setSrc("http://platform.twitter.com/widgets/tweet_button.html?url=" + encode("http://boardcast.it/" + entity
                    .uri()
                    .board()
                    .safe()) + "&text=" + encode("Check out " +
                                                 entity.default$(TITLE, "this board") +
                                                 " on Boardcast #bc") + "&count=horizontal&hashtags=bc&via=boardcast_it");
            footer.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
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
        //        imageSelector.init(Arrays.asList("/_static/_images/wallpapers/light-blue-linen.jpg", "/_static/_images/wallpapers/linen-blue.jpg", "/_static/_images/wallpapers/linen-white.jpg"
        //        ,"/_static/_images/wallpapers/linen-black.jpg", "/_static/_images/wallpapers/noise-white.jpg", "/_static/_images/wallpapers/noise-grey.jpg", "/_static/_images/wallpapers/noise-vlight-grey.jpg"
        //        ,"/_static/_images/wallpapers/noise-black.jpg", "/_static/_images/wallpapers/noise-black.jpg"));


        final boolean adminPermission = $().$bool(ADMINISTERABLE);
        Window.setTitle("Boardcast : " + $().$(TITLE));

        if (previousUri == null || !previousUri.equals(uri)) {
            $.async(new Runnable() {
                @Override public void run() {
                    content.init($(), threadSafeExecutor);
                    final String snapshotUrl = "http://boardcast.it/_snapshot-" + shortUrl + "?bid=" + System.currentTimeMillis();
                    final String imageUrl = "/_website-snapshot?url="
                                            + URL.encode(snapshotUrl)
                                            + "&size=LARGE&width=150&height=200&delayAsync=60";
                    if ($().$bool(MODIFIABLE)) {
                        menuBar.init(PublicBoard.this, $(), true, getChangeBackgroundDialog());
                        removeStyleName("readonly");
                    } else {
                        menuBar.init(PublicBoard.this, $(), false, getChangeBackgroundDialog());
                    }
                    removeStyleName("loading");
                    StartupUtil.showLiveVersion(getWidget().getElement());
                }
            });


            $.async(new Runnable() {
                @Override public void run() {
                    comments.clear();
                    comments.init(uri);
                    if (ClientApplicationConfiguration.isAlphaFeatures()) {
                        notificationPanel.init(uri);
                    }
                    addCommentBox.init(uri);
                }
            });
        }

    }

    //    private static native void setShareThisDetails(String board, String title, String summary, String image, Element element) /*-{
    //        $wnd.stWidget.addEntry({
    //            "service": "sharethis",
    //            "element": element,
    //            "url": "http://boardca.st/" + board,
    //            "title": title,
    //            "image": image,
    //            "summary": summary,
    //            "text": "Share"
    //        });
    //
    //    }-*/;

    @Nonnull
    private String buildVisibilityDescription() {
        String description = "";
        if (entity == null) {
            return "";
        }
        if (entity.$bool(LISTED)) {
            description += "It is a listed board which is ";
            if (entity.allowed(WORLD_SCOPE, VIEW_PERM)) {
                description += "visible to all";
                if (entity.allowed(WORLD_SCOPE, EDIT_PERM)) {
                    description += " and editable by all.";
                } else {
                    if (entity.allowed(WORLD_SCOPE, MODIFY_PERM)) {
                        description += " and modifiable by all.";
                    } else {
                        description += ". ";
                    }
                }
            } else {
                description += "currently only visible to the creator.";
            }
        } else {
            description += "It is an unlisted board which is ";
            if (entity.allowed(WORLD_SCOPE, VIEW_PERM)) {
                description += "visible to those who know the URL";
                if (entity.allowed(WORLD_SCOPE, EDIT_PERM)) {
                    description += " and editable by them. ";
                } else {
                    if (entity.allowed(WORLD_SCOPE, EDIT_PERM)) {
                        description += " and modifiable by them. ";
                    } else {
                        description += ". ";
                    }
                }
            } else {
                description += "visible only to the creator.";
            }
        }
        return description;
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
        //sharethis button
        //        final RootPanel sharethis = RootPanel.get("sharethisbutton");
        //        sharethisElement = sharethis.getElement();
        //        sharethisElement.removeFromParent();
        //        sharethisElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
        //        shareThisHolder.getElement().appendChild(sharethis.getElement());

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

    //TODO: Change all this into a proper command class type thing.
    private class LockIconClickHandler implements ClickHandler {
        private final boolean lock;

        public LockIconClickHandler(final boolean lock) {
            this.lock = lock;
        }

        @Override
        public void onClick(final ClickEvent event) {
            final PermissionChangeType change;
            //            PermissionSet permissionSet = PermissionSet.createPermissionSet($().$(Attribute.PERMISSIONS));
            if (lock) {
                change = MAKE_PUBLIC_READONLY;
            } else {
                change = MAKE_PUBLIC;
            }

            BusFactory.get()
                      .send(new ChangePermissionRequest(uri, change), new AbstractResponseCallback<ChangePermissionRequest>() {
                          @Override
                          public void onFailure(final ChangePermissionRequest message, @Nonnull final ChangePermissionRequest response) {
                              Window.alert("Failed to (un)lock.");
                          }
                      });
        }
    }
}