package cazcade.boardcast.client.main.widgets.board;

import cazcade.boardcast.client.main.widgets.AddChatBox;
import cazcade.boardcast.client.main.widgets.AddCommentBox;
import cazcade.boardcast.client.main.widgets.BoardMenuBar;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.*;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import cazcade.vortex.widgets.client.profile.ProfileBoardHeader;
import cazcade.vortex.widgets.client.stream.ChatStreamPanel;
import cazcade.vortex.widgets.client.stream.CommentPanel;
import cazcade.vortex.widgets.client.stream.NotificationPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.*;
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
import com.google.gwt.user.client.ui.RootPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PublicBoard extends EntityBackedFormPanel {
    private static final NewBoardUiBinder ourUiBinder = GWT.create(NewBoardUiBinder.class);


    @UiField
    CommentPanel comments;
    @UiField
    AddCommentBox addCommentBox;

    @UiField
    PoolContentArea contentArea;
    @UiField
    BoardMenuBar menuBar;
    //    @UiField
//    DivElement boardLockedIcon;
//    @UiField
//    HTMLPanel shareThisHolder;
    @UiField
    AliasDetailFlowPanel ownerDetailPanel;
    @UiField
    SpanElement authorFullname;
    @UiField
    SpanElement publishDate;
    @UiField
    PublicBoardHeader publicBoardHeader;
    @UiField
    ProfileBoardHeader profileBoardHeader;
    @UiField
    NotificationPanel notificationPanel;
    @UiField
    DivElement footer;
    @UiField
    IFrameElement tweetButton;
    @UiField
    SpanElement visibilityDescription;
    @UiField
    DivElement containerDiv;
    @UiField
    AddChatBox addChatBox;
    @UiField
    ChatStreamPanel stream;

    private long updatePoolListener;
    private ChangeBackgroundDialog changeBackgroundDialog;
    private boolean inited;


    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    private LiquidURI poolURI;
    private LiquidURI previousPoolURI;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private long changePermissionListener;
    private boolean chatMode;
//    private Element sharethisElement;


    private static native void replaceState(String title, String state) /*-{
        if (window.history.replaceState != 'undefined') {
            window.history.replaceState(state, title, state);
        }
    }-*/;

    public PublicBoard() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        WidgetUtil.hide(getWidget(), false);
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                sizeNotificationPanel();
            }
        }
                               );
    }

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
        navigate(token);
    }

    public void navigate(@Nullable final String value) {
        Track.getInstance().trackEvent("Board", value);
        if (value == null || value.startsWith(".") || value.startsWith("_") || value.isEmpty()) {
            Window.alert("Invalid board name " + value);
            return;
        }
        if (poolURI != null && poolURI.asShortUrl().asUrlSafe().equalsIgnoreCase(value)) {
            return;
        }
        previousPoolURI = poolURI;
        poolURI = new LiquidURI(LiquidBoardURL.convertFromShort(value));
        if (isAttached()) {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(final Throwable reason) {
                    ClientLog.log(reason);
                }

                @Override
                public void onSuccess() {
                    refresh();
                }
            }
                        );
        }
    }

    /*
    private void addUserInfo() {
        final LSDTransferEntity alias = UserUtil.getCurrentAlias();
        if (alias == null || UserUtil.isAnonymousOrLoggedOut()) {
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
            userImage.setUrl(alias.getAttribute(LSDAttribute.ICON_URL));
            userFullName.setText(alias.getAttribute(LSDAttribute.FULL_NAME));
            userFullName.sinkEvents(Event.ONCLICK);
//            userImage.sinkEvents(Event.ONCLICK);
//            userImage.addHandler(new ChangeProfileClickHandler(alias), ClickEvent.getType());
            userFullName.addClickHandler(new ChangeProfileClickHandler(alias));
            userImage.addClickHandler(new ChangeProfileClickHandler(alias));

        }
    }
    */

    private void refresh() {
        if (changePermissionListener != 0) {
            BusFactory.getInstance().removeListener(changePermissionListener);
        }

        changePermissionListener = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(poolURI,
                                                                                                 LiquidRequestType.CHANGE_PERMISSION,
                                                                                                 new BusListener() {
                                                                                                     @Override
                                                                                                     public void handle(
                                                                                                             final LiquidMessage message) {
                                                                                                         Window.alert(
                                                                                                                 "The access rights have just changed for this board, please refresh the page in your browser."
                                                                                                                     );
//                refresh();
                                                                                                     }
                                                                                                 }
                                                                                                );

        if (updatePoolListener != 0) {
            BusFactory.getInstance().removeListener(updatePoolListener);
        }

        updatePoolListener = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(poolURI, LiquidRequestType.UPDATE_POOL,
                                                                                           new BusListener() {
                                                                                               @Override
                                                                                               public void handle(
                                                                                                       final LiquidMessage response) {
                                                                                                   update((LiquidRequest) response);
                                                                                               }
                                                                                           }
                                                                                          );


        final boolean listed = poolURI.asShortUrl().isListedByConvention();
        //start listed boards as public readonly, default is public writeable
        if (previousPoolURI == null || !previousPoolURI.equals(poolURI)) {
            contentArea.clear();
        }
        bus.send(new VisitPoolRequest(LSDDictionaryTypes.BOARD, poolURI, previousPoolURI, !UserUtil.isAnonymousOrLoggedOut(),
                                      listed, listed ? LiquidPermissionChangeType.MAKE_PUBLIC_READONLY : null
        ), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override
            public void onFailure(final VisitPoolRequest message, @Nonnull final VisitPoolRequest response) {
                if (response.getResponse().getTypeDef().canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    if (UserUtil.isAnonymousOrLoggedOut()) {
                        Window.alert("Please login first.");
                    }
                    else {
                        Window.alert("You don't have permission");
                    }
                }
                else {
                    super.onFailure(message, response);
                }
            }

            @Override
            public void onSuccess(final VisitPoolRequest message, @Nonnull final VisitPoolRequest response) {
                final LSDTransferEntity responseEntity = response.getResponse();
                if (responseEntity == null || responseEntity.canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    Window.alert("Why not sign up to create new boards?");
                    if (previousPoolURI != null) {
                        HistoryManager.navigate(previousPoolURI.asShortUrl().toString());
                    }
                }
                else if (responseEntity.canBe(LSDDictionaryTypes.POOL)) {
                    bind(responseEntity.copy());
                }
                else {
                    Window.alert(responseEntity.getAttribute(LSDAttribute.TITLE));
                }
            }
        }
                );
    }

    private void update(@Nonnull final LiquidRequest response) {
        bind(response.getResponse().copy());
    }

    public void bind(final LSDTransferEntity entity) {
        super.bind(entity);
        addBinding(getChangeBackgroundDialog(), LSDAttribute.IMAGE_URL);
//        addBinding(text, LSDAttribute.TEXT_EXTENDED);
    }

    @Nonnull
    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull
    @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        setEntity(response.getResponse().copy());
                    }

                    @Override
                    public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
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
        notificationPanel.getElement().getStyle().setLeft(contentArea.getAbsoluteLeft(), Style.Unit.PX);
        notificationPanel.getElement().getStyle().setRight(
                Window.getClientWidth() - (contentArea.getAbsoluteLeft() + contentArea.getOffsetWidth()), Style.Unit.PX
                                                          );
    }

    @Override
    protected void onChange(@Nonnull final LSDBaseEntity entity) {
        addStyleName("readonly");
        addStyleName("loading");
        final LSDBaseEntity owner = getEntity().getSubEntity(LSDAttribute.OWNER, true);

        if (entity.getBooleanAttribute(LSDAttribute.MODIFIABLE)) {
            addStyleName("modifiable-board");
        }
        else {
            removeStyleName("modifiable-board");
        }

        if (!getEntity().getURI().asShortUrl().isProfileBoard()) {
            publicBoardHeader.bind(getEntity());
            publicBoardHeader.setVisible(true);
            profileBoardHeader.setVisible(false);
            footer.getStyle().setVisibility(Style.Visibility.VISIBLE);
            ownerDetailPanel.setAliasURI(owner.getURI());
            ownerDetailPanel.setVisible(!UserUtil.isAlias(owner.getURI()));
//            replaceState("Boardcast : " + getEntity().getAttribute(LSDAttribute.TITLE), "/" + getEntity().getAttribute(LSDAttribute.NAME));
            tweetButton.setSrc("http://platform.twitter.com/widgets/tweet_button.html?url=" + encode(
                    "http://boardcast.it/" + entity.getURI().asShortUrl().asUrlSafe()
                                                                                                    ) +
                               "&text=" + encode("Check out " +
                                                 entity.getAttribute(LSDAttribute.TITLE, "this board") +
                                                 " on Boardcast #bc"
                                                )
                               + "&count=horizontal"
                              );
        }
        else {
            profileBoardHeader.setVisible(true);
            publicBoardHeader.setVisible(false);
            ownerDetailPanel.setVisible(false);
            footer.getStyle().setVisibility(Style.Visibility.HIDDEN);
            profileBoardHeader.setAliasURI(owner.getURI());
//            replaceState("Boardcast : User : " + owner.getAttribute(LSDAttribute.FULL_NAME), "/~" + getEntity().getAttribute(LSDAttribute.NAME));
            tweetButton.setSrc("http://platform.twitter.com/widgets/tweet_button.html?url=" + encode(
                    "http://boardcast.it/" + entity.getURI().asShortUrl().asUrlSafe()
                                                                                                    ) +
                               "&text=" + encode("Check out this profile on Boardcast #bc")
                               + "&count=horizontal"
                              );
        }
        authorFullname.setInnerText(owner.getAttribute(LSDAttribute.FULL_NAME));
        final Date published = entity.getPublished();
        if (published != null) {
            publishDate.setInnerText(published.toString());
        }
        final String shortUrl = entity.getURI().asShortUrl().asUrlSafe();
        String tagText = "";
        if (!shortUrl.startsWith("-")) {
            tagText = " The hashtag for this board is #" +
                      shortUrl +
                      ", the tag can be placed in comments and text and will link " +
                      "back to this board.";
        }
        visibilityDescription.setInnerText(buildVisibilityDescription() + " There have been " + entity.getAttribute(
                LSDAttribute.VISITS_METRIC
                                                                                                                   )
                                           +
                                           " visits including " +
                                           entity.getAttribute(LSDAttribute.REGISTERED_VISITORS_METRIC) +
                                           " registered users and " +
                                           entity.getAttribute(LSDAttribute.COMMENT_COUNT, "no") +
                                           " comments left." + tagText
                                          );
//        imageSelector.init(Arrays.asList("_images/wallpapers/light-blue-linen.jpg", "_images/wallpapers/linen-blue.jpg", "_images/wallpapers/linen-white.jpg"
//        ,"_images/wallpapers/linen-black.jpg", "_images/wallpapers/noise-white.jpg", "_images/wallpapers/noise-grey.jpg", "_images/wallpapers/noise-vlight-grey.jpg"
//        ,"_images/wallpapers/noise-black.jpg", "_images/wallpapers/noise-black.jpg"));

        final boolean adminPermission = entity.getBooleanAttribute(LSDAttribute.ADMINISTERABLE);
        final String boardTitle = entity.getAttribute(LSDAttribute.TITLE);
        Window.setTitle("Boardcast : " + boardTitle);


        if (previousPoolURI == null || !previousPoolURI.equals(poolURI)) {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(final Throwable reason) {
                    ClientLog.log(reason);
                }

                @Override
                public void onSuccess() {
                    contentArea.init(getEntity(), FormatUtil.getInstance(), threadSafeExecutor);
                    final String snapshotUrl = "http://boardcast.it/_snapshot-" + shortUrl + "?bid=" + System.currentTimeMillis();
                    final String imageUrl = "/_image-service?url=" + URL.encode(snapshotUrl)
                                            + "&size=LARGE&width=150&height=200&delay=60";


//                    <img class="thumbnail"
//                    src='<c:url value="_image-service">
//                        <c:param name="url" value="${board.snapshotUrl}"/>
//                    <c:param name="text" value="${board.title}"/>
//                    <c:param name="size" value="LARGE"/>
//                    <c:param name="width" value="300"/>
//                    <c:param name="height" value="400"/>
//                    <c:param name="delay" value="60"/>
//                    </c:url>'
//                    alt="${board.description}"/>

                    //bottom toolbar
                    configureShareThis(imageUrl, boardTitle, shortUrl);
                    if (getEntity().getBooleanAttribute(LSDAttribute.MODIFIABLE)) {
                        menuBar.init(PublicBoard.this, getEntity(), true, getChangeBackgroundDialog());
                        removeStyleName("readonly");
                    }
                    else {
                        menuBar.init(PublicBoard.this, getEntity(), false, getChangeBackgroundDialog());
                    }
                    StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
                    WidgetUtil.showGracefully(getWidget(), false);
                    removeStyleName("loading");
                }
            }
                        );
        }


        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(final Throwable reason) {
                ClientLog.log(reason);
            }

            @Override
            public void onSuccess() {
                comments.clear();

                comments.init(poolURI, FormatUtil.getInstance());
                if (ClientApplicationConfiguration.isAlphaFeatures()) {
                    notificationPanel.init(poolURI, FormatUtil.getInstance());
                }
                addCommentBox.init(poolURI);
            }
        }
                    );
    }

    private void configureShareThis(String imageUrl, String boardTitle, String board) {
        final NodeList<Element> spans = RootPanel.get("sharethisbar").getElement().getElementsByTagName(
                "span"
                                                                                                       );
        final int max = spans.getLength();
        for (int i = 0; i < max; i++) {
            final Element span = spans.getItem(i);
            if (span.hasAttribute("class") && "stButton".equalsIgnoreCase(span.getAttribute("class"))) {
                setShareThisDetails(board, "Take a look at the Boardcast board '" + boardTitle + "' ", "",
                                    imageUrl == null ? "" : imageUrl, span
                                   );
            }

        }
    }

    @Nonnull
    private String buildVisibilityDescription() {
        String description = "";
        if (entity == null) {
            return "";
        }
        if (entity.getBooleanAttribute(LSDAttribute.LISTED)) {
            description += "It is a listed board which is ";
            if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
                description += "visible to all";
                if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT)) {
                    description += " and editable by all.";
                }
                else {
                    if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                        description += " and modifiable by all.";
                    }
                    else {
                        description += ". ";
                    }
                }
            }
            else {
                description += "currently only visible to the creator.";
            }
        }
        else {
            description += "It is an unlisted board which is ";
            if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
                description += "visible to those who know the URL";
                if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT)) {
                    description += " and editable by them. ";
                }
                else {
                    if (entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT)) {
                        description += " and modifiable by them. ";
                    }
                    else {
                        description += ". ";
                    }
                }
            }
            else {
                description += "visible only to the creator.";
            }
        }
        return description;
    }

    private static native void setShareThisDetails(String board, String title, String summary, String image, Element element) /*-{
        $wnd.stWidget.addEntry({
            "service":"sharethis",
            "element":element,
            "url":"http://boardca.st/" + board,
            "title":title,
            "image":image,
            "summary":summary,
            "text":"Share"
        });

    }-*/;


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
        if (poolURI != null) {
            refresh();
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
        }
        else {
            getWidget().addStyleName("chat");
            addChatBox.init(poolURI);
            stream.init(poolURI);
        }
        chatMode = !chatMode;
    }

    interface NewBoardUiBinder extends UiBinder<HTMLPanel, PublicBoard> {
    }

    //TODO: Change all this into a proper command class type thing.
    private class LockIconClickHandler implements ClickHandler {
        private final boolean lock;

        public LockIconClickHandler(final boolean lock) {
            this.lock = lock;
        }

        @Override
        public void onClick(final ClickEvent event) {
            final LiquidPermissionChangeType change;
//            LiquidPermissionSet permissionSet = LiquidPermissionSet.createPermissionSet(getEntity().getAttribute(LSDAttribute.PERMISSIONS));
            if (lock) {
                change = LiquidPermissionChangeType.MAKE_PUBLIC_READONLY;
            }
            else {
                change = LiquidPermissionChangeType.MAKE_PUBLIC;
            }

            BusFactory.getInstance().send(new ChangePermissionRequest(poolURI, change),
                                          new AbstractResponseCallback<ChangePermissionRequest>() {
                                              @Override
                                              public void onSuccess(final ChangePermissionRequest message,
                                                                    final ChangePermissionRequest response) {
                                              }

                                              @Override
                                              public void onFailure(final ChangePermissionRequest message,
                                                                    @Nonnull final ChangePermissionRequest response) {
                                                  Window.alert("Failed to (un)lock.");
                                              }
                                          }
                                         );
        }
    }
}