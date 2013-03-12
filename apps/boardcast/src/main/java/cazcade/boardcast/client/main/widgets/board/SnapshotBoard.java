/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.lsd.Dictionary.TITLE;
import static cazcade.liquid.api.lsd.Types.T_POOL;
import static cazcade.liquid.api.lsd.Types.T_RESOURCE_NOT_FOUND;

/**
 * @author neilellis@cazcade.com
 */
public class SnapshotBoard extends EntityBackedFormPanel {
    private static final NewBoardUiBinder ourUiBinder                   = GWT.create(NewBoardUiBinder.class);
    public static final  int              WAIT_UNTIL_READY_FOR_SNAPSHOT = 5 * 1000;


    @UiField PoolContentArea contentArea;

    private long    updatePoolListener;
    private boolean inited;


    @Nonnull
    private final BusService bus = Bus.get();
    private LURI poolURI;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    public SnapshotBoard() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        WidgetUtil.hide(getWidget(), false);
        //        if (embedded) {
        //            addStyleName("embedded-board");
        //        }
    }

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
        navigate(token);
    }

    public void navigate(@Nullable final String value) {
        if (value == null || value.startsWith(".") || value.startsWith("_") || value.isEmpty()) {
            Window.alert("Invalid board name " + value);
            return;
        }
        if (poolURI != null && poolURI.board().safe().equalsIgnoreCase(value)) {
            return;
        }
        poolURI = new LURI(BoardURL.from(value));
        if (isAttached()) {
            final Runnable runnable1 = new Runnable() {
                @Override public void run() {
                    refresh();
                }
            };
            GWT.runAsync(new RunAsyncCallback() {
                private final Runnable runnable = runnable1;

                @Override public void onFailure(Throwable reason) {
                    ClientLog.log(reason);
                }

                @Override public void onSuccess() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        ClientLog.log(e);
                    }
                }
            });


        }
    }

    private void refresh() {
        if (updatePoolListener != 0) {
            Bus.get().remove(updatePoolListener);
        }

        updatePoolListener = Bus.get().listenForSuccess(poolURI, RequestType.R_UPDATE_POOL, new BusListener() {
            @Override
            public void handle(final LiquidMessage response) {
                update((LiquidRequest) response);
            }
        });


        final boolean listed = poolURI.board().listedConvention();
        //start listed boards as public readonly, default is public writeable
        contentArea.clear();
        bus.send(new RetrievePoolRequest(poolURI, true, false), new AbstractMessageCallback<RetrievePoolRequest>() {
            @Override
            public void onFailure(final RetrievePoolRequest original, @Nonnull final RetrievePoolRequest message) {
                if (message.response().type().canBe(T_RESOURCE_NOT_FOUND)) {
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
            public void onSuccess(final RetrievePoolRequest original, @Nonnull final RetrievePoolRequest message) {
                final TransferEntity resp = message.response();
                if (resp.canBe(T_RESOURCE_NOT_FOUND)) {
                    Window.alert("Why not sign up to create new boards?");
                } else if (resp.canBe(T_POOL)) {
                    $(resp.$());
                } else {
                    Window.alert(resp.$(TITLE));
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
    protected Runnable getUpdateEntityAction(final Bindable field) {
        throw new UnsupportedOperationException("Readonly snapshot board.");
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    protected void onChange(final Entity entity) {
        addStyleName("readonly");
        addStyleName("loading");
        Window.setTitle("Boardcast : " + $().$(TITLE));
        contentArea.init($(), threadSafeExecutor);
        StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
        WidgetUtil.showGracefully(getWidget(), false);
        removeStyleName("loading");
        new Timer() {
            @Override
            public void run() {
                Window.setStatus("snapshot-loaded");
            }
        }.schedule(WAIT_UNTIL_READY_FOR_SNAPSHOT);
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
        if (poolURI != null) {
            refresh();
        }
    }

    interface NewBoardUiBinder extends UiBinder<HTMLPanel, SnapshotBoard> {}
}