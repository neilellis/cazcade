package cazcade.hashbo.client.main.widgets.board;

import cazcade.hashbo.client.main.widgets.BoardMenuBar;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.hashbo.client.StartupUtil;
import cazcade.hashbo.client.main.widgets.AddCommentBox;
import cazcade.hashbo.client.main.widgets.toolbar.HashboToolbarIcon;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.image.ImageOption;
import cazcade.vortex.widgets.client.image.ImageSelection;
import cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import cazcade.vortex.widgets.client.profile.ProfileBoardHeader;
import cazcade.vortex.widgets.client.stream.CommentPanel;
import cazcade.vortex.widgets.client.stream.NotificationPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author neilellis@cazcade.com
 */
public class PublicBoard extends EntityBackedFormPanel {

    private long updatePoolListener;

    public void bind(LSDEntity entity) {
        super.bind(entity);
        addBinding(changeBackgroundPanel, LSDAttribute.IMAGE_URL);
        addBinding(text, LSDAttribute.TEXT_EXTENDED);
    }

    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {

                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolRequest message, UpdatePoolRequest response) {
                        setEntity(response.getResponse());
                    }

                    @Override
                    public void onFailure(UpdatePoolRequest message, UpdatePoolRequest response) {
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }


                });
            }
        };
    }

    public void navigate(String value) {
        if (value == null || value.startsWith(".") || value.isEmpty()) {
            Window.alert("Invalid board name " + value);
        }
        previousPoolURI = poolURI;
        poolURI = new LiquidURI(LiquidBoardURL.convertFromShort(value));
        if (isAttached()) {
            refresh();
        }
    }

    @Override
    public void onLocalHistoryTokenChanged(String token) {
        navigate(token);
    }

    interface NewBoardUiBinder extends UiBinder<HTMLPanel, PublicBoard> {
    }

    private static NewBoardUiBinder ourUiBinder = GWT.create(NewBoardUiBinder.class);


    private Bus bus = BusFactory.getInstance();
    private LiquidURI poolURI;
    private LiquidURI previousPoolURI;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private LSDEntity entity;
    private long changePermissionListener;
    private Element sharethisElement;


    @Override
    protected void onLoad() {
        super.onLoad();
        //sharethis button
        RootPanel sharethis = RootPanel.get("sharethis");
        sharethisElement = sharethis.getElement();
        sharethisElement.removeFromParent();
        sharethisElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
        shareThisHolder.getElement().appendChild(sharethis.getElement());

        addCommentBox.sinkEvents(Event.MOUSEEVENTS);
        lockIcon.addHandler(new LockIconClickHandler(true), ClickEvent.getType());
        unlockIcon.addHandler(new LockIconClickHandler(false), ClickEvent.getType());
        personalIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("This will create a personal version of the board. Feature coming soon ...");
            }
        }, ClickEvent.getType());
        if (poolURI != null) {
            refresh();
        }

    }
    /*
    private void addUserInfo() {
        final LSDEntity alias = UserUtil.getCurrentAlias();
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

        changePermissionListener = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(poolURI, LiquidRequestType.CHANGE_PERMISSION, new BusListener() {
            @Override
            public void handle(LiquidMessage message) {
                Window.alert("The access rights have just changed for this board, please refresh the page in your browser.");
//                refresh();
            }
        });

        if (updatePoolListener != 0) {
            BusFactory.getInstance().removeListener(updatePoolListener);
        }

        updatePoolListener = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(poolURI, LiquidRequestType.UPDATE_POOL, new BusListener() {
            @Override
            public void handle(LiquidMessage response) {
                update((LiquidRequest) response);

            }
        });


        final boolean listed = poolURI.asShortUrl().isListedByConvention();
        //start listed boards as public readonly, default is public writeable
        bus.send(new VisitPoolRequest(LSDDictionaryTypes.BOARD, poolURI, previousPoolURI, !UserUtil.isAnonymousOrLoggedOut(), listed, listed ? LiquidPermissionChangeType.MAKE_PUBLIC_READONLY : null), new AbstractResponseCallback<VisitPoolRequest>() {

            @Override
            public void onFailure(VisitPoolRequest message, VisitPoolRequest response) {
                if (response.getResponse().getTypeDef().canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    if (UserUtil.isAnonymousOrLoggedOut()) {
                        Window.alert("Please login first.");
                    } else {
                        Window.alert("You don't have permission");
                    }
                } else {
                    super.onFailure(message, response);
                }
            }

            @Override
            public void onSuccess(VisitPoolRequest message, final VisitPoolRequest response) {
                if (response.getResponse() == null || response.getResponse().canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    Window.alert("Why not sign up to create new boards?");
                    if (previousPoolURI != null) {
                        History.newItem(previousPoolURI.asShortUrl().toString());
                    }
                } else if (response.getResponse().canBe(LSDDictionaryTypes.POOL)) {
                    bind(response.getResponse());
                } else {
                    Window.alert(response.getResponse().getAttribute(LSDAttribute.TITLE));
                }

            }
        });
    }

    private void update(LiquidRequest response) {
        bind(response.getResponse());
    }

    @Override
    protected void onChange(LSDEntity entity) {
        comments.clear();
        LSDEntity owner = getEntity().getSubEntity(LSDAttribute.OWNER);

        if(entity.getBooleanAttribute(LSDAttribute.MODIFIABLE)) {
            addStyleName("modifiable-board");
        } else {
            removeStyleName("modifiable-board");
        }

        if (!getEntity().getURI().asShortUrl().isProfileBoard()) {
            publicBoardHeader.bind(getEntity());
            publicBoardHeader.setVisible(true);
            profileBoardHeader.setVisible(false);
            ownerDetailPanel.setVisible(!UserUtil.isAlias(owner.getURI()));
//            replaceState("Boardcast : " + getEntity().getAttribute(LSDAttribute.TITLE), "/" + getEntity().getAttribute(LSDAttribute.NAME));

        }

        if (getEntity().getURI().asShortUrl().isProfileBoard()) {
            profileBoardHeader.setVisible(true);
            publicBoardHeader.setVisible(false);
            ownerDetailPanel.setVisible(false);
            profileBoardHeader.setAliasURI(owner.getURI());
//            replaceState("Boardcast : User : " + owner.getAttribute(LSDAttribute.FULL_NAME), "/~" + getEntity().getAttribute(LSDAttribute.NAME));
        }
        ownerDetailPanel.setAliasURI(owner.getURI());
        authorFullname.setInnerText(owner.getAttribute(LSDAttribute.FULL_NAME));
        publishDate.setInnerText(getEntity().getPublished().toString());
        imageSelector.setSelectionAction(new ImageSelection.SelectionAction() {
            @Override
            public void onSelect(ImageOption imageOption) {
                changeBackgroundPanel.setValue(imageOption.getUrl());
                changeBackgroundPanel.callOnChangeAction();
            }
        });

//        imageSelector.init(Arrays.asList("_images/wallpapers/light-blue-linen.jpg", "_images/wallpapers/linen-blue.jpg", "_images/wallpapers/linen-white.jpg"
//        ,"_images/wallpapers/linen-black.jpg", "_images/wallpapers/noise-white.jpg", "_images/wallpapers/noise-grey.jpg", "_images/wallpapers/noise-vlight-grey.jpg"
//        ,"_images/wallpapers/noise-black.jpg", "_images/wallpapers/noise-black.jpg"));

        boolean adminPermission = getEntity().getBooleanAttribute(LSDAttribute.ADMINISTERABLE);
        controls.setVisible(adminPermission);

        if (getEntity().hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
            unlockIcon.setVisible(false);
            lockIcon.setVisible(true);
        } else {
            unlockIcon.setVisible(true);
            lockIcon.setVisible(false);
        }

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                ClientLog.log(reason);
            }

            @Override
            public void onSuccess() {
                if (previousPoolURI == null || !previousPoolURI.equals(poolURI)) {
                    contentArea.clear();
                    contentArea.init(getEntity(), FormatUtil.getInstance(), threadSafeExecutor);
                    final String imageUrl = getEntity().getAttribute(LSDAttribute.IMAGE_URL);
                    final String boardTitle = getEntity().getAttribute(LSDAttribute.TITLE);
                    setShareThisDetails(poolURI.asShortUrl().asUrlSafe(), "Take a look at the Boardcast board '" + boardTitle + "' ", "", imageUrl == null ? "" : imageUrl, sharethisElement);
                    if (getEntity().getBooleanAttribute(LSDAttribute.MODIFIABLE)) {
                        menuBar.setUri(poolURI);
                        menuBar.setVisible(true);
                        boardLockedIcon.getStyle().setVisibility(Style.Visibility.HIDDEN);
                    } else {
                        menuBar.setVisible(false);
                        boardLockedIcon.getStyle().setVisibility(Style.Visibility.VISIBLE);

                    }
                    StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
                    WidgetUtil.showGracefully(getWidget(), false);
                }

            }
        });
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                ClientLog.log(reason);
            }

            @Override
            public void onSuccess() {
                comments.init(poolURI, FormatUtil.getInstance());
                notificationPanel.init(poolURI, FormatUtil.getInstance());
                addCommentBox.init(poolURI);
            }
        });
    }


    private native static void replaceState(String title, String state) /*-{
        if(window.history.replaceState != 'undefined') {
            window.history.replaceState(state, title, state);
        }
    }-*/;


    @UiField
    CommentPanel comments;
    @UiField
    AddCommentBox addCommentBox;

    @UiField
    PoolContentArea contentArea;
    @UiField
    BoardMenuBar menuBar;
    @UiField
    HashboToolbarIcon lockIcon;
    @UiField
    HashboToolbarIcon personalIcon;
    @UiField
    DivElement boardLockedIcon;
    @UiField
    HTMLPanel shareThisHolder;
    @UiField
    AliasDetailFlowPanel ownerDetailPanel;
    @UiField
    ChangeImageUrlPanel changeBackgroundPanel;
    @UiField
    ImageSelection imageSelector;
    @UiField
    HashboToolbarIcon unlockIcon;
    @UiField
    HTMLPanel controls;
    @UiField
    SpanElement authorFullname;
    @UiField
    SpanElement publishDate;
    @UiField
    PublicBoardHeader publicBoardHeader;
    @UiField
    ProfileBoardHeader profileBoardHeader;
    @UiField
    VortexEditableLabel text;
    @UiField
    NotificationPanel notificationPanel;

    @Override
    protected void onAttach() {
        super.onAttach();
        sizeNotificationPanel();
    }

    public PublicBoard() {
        initWidget(ourUiBinder.createAndBindUi(this));
        WidgetUtil.hide(getWidget(), false);
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                sizeNotificationPanel();
            }
        });
    }

    private void sizeNotificationPanel() {
        notificationPanel.getElement().getStyle().setLeft(contentArea.getAbsoluteLeft(), Style.Unit.PX);
        notificationPanel.getElement().getStyle().setRight(Window.getClientWidth() - (contentArea.getAbsoluteLeft() + contentArea.getOffsetWidth()), Style.Unit.PX);
    }


    //TODO: Change all this into a proper command class type thing.
    private class LockIconClickHandler implements ClickHandler {
        private boolean lock;

        public LockIconClickHandler(boolean lock) {
            this.lock = lock;
        }

        @Override
        public void onClick(final ClickEvent event) {
            final LiquidPermissionChangeType change;
//            LiquidPermissionSet permissionSet = LiquidPermissionSet.createPermissionSet(getEntity().getAttribute(LSDAttribute.PERMISSIONS));
            if (lock) {
                change = LiquidPermissionChangeType.MAKE_PUBLIC_READONLY;
            } else {
                change = LiquidPermissionChangeType.MAKE_PUBLIC;
            }

            BusFactory.getInstance().send(new ChangePermissionRequest(poolURI, change), new AbstractResponseCallback<ChangePermissionRequest>() {
                @Override
                public void onSuccess(ChangePermissionRequest message, ChangePermissionRequest response) {
                }

                @Override
                public void onFailure(ChangePermissionRequest message, ChangePermissionRequest response) {
                    Window.alert("Failed to (un)lock.");
                }
            });
        }
    }


    private static native void setShareThisDetails(String board, String title, String summary, String image, Element element) /*-{
        $wnd.stWidget.addEntry({
            "service": "sharethis",
            "element": element,
            "url": "http://boardca.st/" + board,
            "title": title,
            "image" : image,
            "summary": summary,
            "text" : "Share"
        });

    }-*/;


}