package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.StartupUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.widgets.PoolContentArea;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class SnapshotBoard extends EntityBackedFormPanel {
    private static final NewBoardUiBinder ourUiBinder = GWT.create(NewBoardUiBinder.class);
    public static final int WAIT_UNTIL_READY_FOR_SNAPSHOT = 10 * 1000;


    @UiField
    PoolContentArea contentArea;

    private long updatePoolListener;
    private boolean inited;


    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    private LiquidURI poolURI;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private Element sharethisElement;

    public SnapshotBoard(final boolean embedded) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        WidgetUtil.hide(getWidget(), false);
        if (embedded) {
            addStyleName("embedded-board");
        }
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
        if (poolURI != null && poolURI.asShortUrl().asUrlSafe().equalsIgnoreCase(value)) {
            return;
        }
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

    private void refresh() {
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
        contentArea.clear();
        bus.send(new RetrievePoolRequest(poolURI, true, false), new AbstractResponseCallback<RetrievePoolRequest>() {
            @Override
            public void onFailure(final RetrievePoolRequest message, @Nonnull final RetrievePoolRequest response) {
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
            public void onSuccess(final RetrievePoolRequest message, @Nonnull final RetrievePoolRequest response) {
                final LSDTransferEntity responseEntity = response.getResponse();
                if (responseEntity == null || responseEntity.canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    Window.alert("Why not sign up to create new boards?");
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
    }

    @Nonnull
    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull
    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        throw new UnsupportedOperationException("Readonly snapshot board.");
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    protected void onChange(final LSDBaseEntity entity) {
        addStyleName("readonly");
        addStyleName("loading");
        final String boardTitle = getEntity().getAttribute(LSDAttribute.TITLE);
        Window.setTitle("Boardcast : " + boardTitle);
        contentArea.init(getEntity(), FormatUtil.getInstance(), threadSafeExecutor);
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

    interface NewBoardUiBinder extends UiBinder<HTMLPanel, SnapshotBoard> {
    }
}