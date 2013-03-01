/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.boardcast.client.main.widgets.AddChatBox;
import cazcade.boardcast.client.main.widgets.BoardMenuBar;
import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.*;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import cazcade.vortex.widgets.client.stream.ChatStreamPanel;
import com.google.gwt.core.client.GWT;
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

    @UiField ChatStreamPanel stream;
    @UiField AddChatBox      addChatBox;

    @UiField PoolContentArea contentArea;
    @UiField BoardMenuBar    menuBar;
    @UiField HTMLPanel       board;
    @UiField HTMLPanel       rhs;
    @UiField ToggleButton    hideReveal;
    @UiField DivElement      boardLockedIcon;
    @UiField Label           returnFromChatButton;
    @Nonnull
    private final BusService bus = Bus.get();
    private LURI     poolURI;
    private BoardURL boardURL;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private LURI           previousPoolURI;
    private TransferEntity poolEntity;
    private long           changePermissionListener;
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
        boardURL = new BoardURL(token);
        previousPoolURI = poolURI;
        poolURI = boardURL.asURI();
        refresh();
    }

    private void refresh() {
        //        inbox.setFeatures(FormatUtil.getInstance());
        if (changePermissionListener != 0) {
            Bus.get().remove(changePermissionListener);
        }

        changePermissionListener = Bus.get().listenForSuccess(poolURI, RequestType.R_CHANGE_PERMISSION, new BusListener() {
            @Override
            public void handle(final LiquidMessage message) {
                refresh();
            }
        });


        bus.send(new VisitPoolRequest(Types.T_BOARD, poolURI, previousPoolURI, !User.anon(), poolURI.board()
                                                                                                        .listedConvention()), new AbstractMessageCallback<VisitPoolRequest>() {
            @Override
            public void onFailure(final VisitPoolRequest original, @Nonnull final VisitPoolRequest message) {
                if (message.response().type().canBe(Types.T_RESOURCE_NOT_FOUND)) {
                    if (User.anon()) {
                        Window.alert("Please login first.");
                    } else {
                        Window.alert("You don't have permission");
                    }
                } else {
                    super.onFailure(original, message);
                }
            }

            @Override
            public void onSuccess(final VisitPoolRequest original, @Nonnull final VisitPoolRequest message) {
                StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());

                ClientLog.log("Got response.");
                poolEntity = message.response().$();
                $.async(new Runnable() {
                    @Override public void run() {
                        ClientLog.log(poolEntity.dump());
                        contentArea.clear();
                        contentArea.init(poolEntity, threadSafeExecutor);
                    }
                });
                $.async(new Runnable() {
                    @Override public void run() {
                        if (poolEntity.has(Dictionary.IMAGE_URL)) {
                            contentArea.setBackgroundImage(poolEntity.$(Dictionary.IMAGE_URL));
                        }
                        if (poolEntity.$bool(Dictionary.MODIFIABLE)) {
                            //                    addMenuBarSubMenu.addItem("Decoration", new CreateImageCommand(poolURI, Types.BITMAP_IMAGE_2D));
                            boardLockedIcon.getStyle().setVisibility(Style.Visibility.HIDDEN);
                            //                            menuBar.init(poolEntity, true, null);
                        } else {
                            //                            menuBar.init(poolEntity, false, null);
                            boardLockedIcon.getStyle().setVisibility(Style.Visibility.VISIBLE);
                        }
                    }
                });
                $.async(new Runnable() {
                    @Override public void run() {
                        stream.init(poolURI);
                        addChatBox.init(poolURI);
                    }
                });

            }
        });
    }

    @Override protected boolean isSaveOnExit() {
        return false;
    }

    @Nonnull @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                throw new UnsupportedOperationException("Don't support updates to board from Chat View");
            }
        };
    }

    @Override
    protected void onChange(final Entity entity) {
        super.onChange(entity);
        refresh();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        stream.sinkEvents(Event.MOUSEEVENTS);
        //        stream.addHandler(new RHSMouseOverHandler(), MouseOverEvent.type());
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
                } else {
                    hideRhs();
                }
            }
        });
        hideReveal.setValue(ClientPreferences.booleanPreference(ClientPreferences.Preference.RHS_HIDE), true);

        returnFromChatButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                HistoryManager.get().navigate(boardURL.toString());
            }
        });
    }

    private void showRhs() {
        rhs.getElement().getStyle().setRight(0, Style.Unit.PX);
    }

    private void hideRhs() {
        rhs.getElement().getStyle().setRight(Window.getClientWidth() - (PoolContentArea.DEFAULT_WIDTH + 500), Style.Unit.PX);
    }

    interface BoardUiBinder extends UiBinder<HTMLPanel, BoardcastChatView> {}

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