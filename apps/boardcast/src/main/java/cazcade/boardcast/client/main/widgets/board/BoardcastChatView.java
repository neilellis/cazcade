package cazcade.boardcast.client.main.widgets.board;

import cazcade.boardcast.client.main.widgets.AddChatBox;
import cazcade.boardcast.client.main.widgets.BoardMenuBar;
import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.ClientPreferences;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import cazcade.vortex.widgets.client.stream.ChatStreamPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
@Deprecated
public class BoardcastChatView extends EntityBackedFormPanel {
    @Nonnull
    public static final String RHS_MINIMIZED = "rhs-minimized";


    private static final BoardUiBinder ourUiBinder = GWT.create(BoardUiBinder.class);

    @UiField
    ChatStreamPanel stream;
    @UiField
    AddChatBox addChatBox;

    @UiField
    PoolContentArea contentArea;
    @UiField
    BoardMenuBar menuBar;
    @UiField
    HTMLPanel board;
    @UiField
    HTMLPanel rhs;
    @UiField
    ToggleButton hideReveal;
    @UiField
    DivElement boardLockedIcon;
    @UiField
    Label returnFromChatButton;
    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    private LiquidURI poolURI;
    private LiquidBoardURL boardURL;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private LiquidURI previousPoolURI;
    private LSDTransferEntity poolEntity;
    private long changePermissionListener;
//    @UiField
//    TabLayoutPanel communicationTabPanel;
//    @UiField
//    InboxPanel inbox;
//    @UiField
//    ActivityStreamPanel activity;


    public BoardcastChatView() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void onLocalHistoryTokenChanged(@Nonnull final String token) {
        boardURL = new LiquidBoardURL(token);
        previousPoolURI = poolURI;
        poolURI = boardURL.asURI();
        refresh();
    }

    private void refresh() {
//        inbox.setFeatures(FormatUtil.getInstance());
        if (changePermissionListener != 0) {
            BusFactory.getInstance().removeListener(changePermissionListener);
        }

        changePermissionListener = BusFactory.getInstance().listenForURIAndSuccessfulRequestType(poolURI,
                                                                                                 LiquidRequestType.CHANGE_PERMISSION,
                                                                                                 new BusListener() {
                                                                                                     @Override
                                                                                                     public void handle(
                                                                                                             final LiquidMessage message) {
                                                                                                         refresh();
                                                                                                     }
                                                                                                 }
                                                                                                );


        bus.send(new VisitPoolRequest(LSDDictionaryTypes.BOARD, poolURI, previousPoolURI, !UserUtil.isAnonymousOrLoggedOut(),
                                      poolURI.asShortUrl().isListedByConvention()
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
                StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());

                ClientLog.log("Got response.");
                poolEntity = response.getResponse().copy();
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onFailure(final Throwable reason) {
                        ClientLog.log(reason);
                    }

                    @Override
                    public void onSuccess() {
                        ClientLog.log(poolEntity.dump());
                        contentArea.clear();
                        contentArea.init(poolEntity, FormatUtil.getInstance(), threadSafeExecutor);
                    }
                }
                            );
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onFailure(final Throwable reason) {
                        ClientLog.log(reason);
                    }

                    @Override
                    public void onSuccess() {
                        if (poolEntity.hasAttribute(LSDAttribute.IMAGE_URL)) {
                            contentArea.setBackgroundImage(poolEntity.getAttribute(LSDAttribute.IMAGE_URL));
                        }
                        if (poolEntity.getBooleanAttribute(LSDAttribute.MODIFIABLE)) {
//                    addMenuBarSubMenu.addItem("Decoration", new CreateImageCommand(poolURI, LSDDictionaryTypes.BITMAP_IMAGE_2D));
                            boardLockedIcon.getStyle().setVisibility(Style.Visibility.HIDDEN);
//                            menuBar.init(poolEntity, true, null);
                        }
                        else {
//                            menuBar.init(poolEntity, false, null);
                            boardLockedIcon.getStyle().setVisibility(Style.Visibility.VISIBLE);
                        }
                    }
                }
                            );
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onFailure(final Throwable reason) {
                        ClientLog.log(reason);
                    }

                    @Override
                    public void onSuccess() {
                        stream.init(poolURI);
                        addChatBox.init(poolURI);
                    }
                }
                            );
            }
        }
                );
    }

    @Nonnull
    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull
    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                throw new UnsupportedOperationException("Don't support updates to board from Chat View");
            }
        };
    }

    @Override
    protected void onChange(final LSDBaseEntity entity) {
        super.onChange(entity);
        refresh();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        stream.sinkEvents(Event.MOUSEEVENTS);
//        stream.addHandler(new RHSMouseOverHandler(), MouseOverEvent.getType());
        rhs.sinkEvents(Event.MOUSEEVENTS);
        rhs.addHandler(new RHSMouseOutHandler(), MouseOutEvent.getType());
        addChatBox.sinkEvents(Event.MOUSEEVENTS);
        addChatBox.addHandler(new RHSMouseOverHandler(), MouseOverEvent.getType());


        hideReveal.sinkEvents(Event.MOUSEEVENTS);

        hideReveal.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(@Nonnull final ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                ClientPreferences.setBooleanPreference(ClientPreferences.Preference.RHS_HIDE, booleanValueChangeEvent.getValue());
                if (!booleanValueChangeEvent.getValue()) {
                    showRhs();
                }
                else {
                    hideRhs();
                }
            }
        }
                                        );
        hideReveal.setValue(ClientPreferences.booleanPreference(ClientPreferences.Preference.RHS_HIDE), true);

        returnFromChatButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                HistoryManager.navigate(boardURL.toString());
            }
        }
                                            );
    }

    private void showRhs() {
        rhs.getElement().getStyle().setRight(0, Style.Unit.PX);
    }

    private void hideRhs() {
        rhs.getElement().getStyle().setRight(Window.getClientWidth() - (PoolContentArea.DEFAULT_WIDTH + 500), Style.Unit.PX);
    }

    interface BoardUiBinder extends UiBinder<HTMLPanel, BoardcastChatView> {
    }

    private class RHSMouseOutHandler implements MouseOutHandler {
        @Override
        public void onMouseOut(final MouseOutEvent mouseOutEvent) {
            if (hideReveal.getValue()) {
//                board.addStyleName(RHS_MINIMIZED);
                hideRhs();
            }
        }
    }

    private class RHSMouseOverHandler implements MouseOverHandler {
        @Override
        public void onMouseOver(final MouseOverEvent mouseOverEvent) {
//            board.removeStyleName(RHS_MINIMIZED);
            showRhs();
        }
    }
}