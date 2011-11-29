package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class SnapshotBoard extends EntityBackedFormPanel {

    private long updatePoolListener;
    private boolean inited;

    public void bind(LSDEntity entity) {
        super.bind(entity);
    }

    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        throw new UnsupportedOperationException("Readonly snapshot board.");
    }

    public void navigate(String value) {

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
                public void onFailure(Throwable reason) {
                    ClientLog.log(reason);
                }

                @Override
                public void onSuccess() {
                    refresh();
                }
            });
        }
    }

    @Override
    public void onLocalHistoryTokenChanged(String token) {
        navigate(token);
    }


    interface NewBoardUiBinder extends UiBinder<HTMLPanel, SnapshotBoard> {
    }

    private static NewBoardUiBinder ourUiBinder = GWT.create(NewBoardUiBinder.class);


    private Bus bus = BusFactory.getInstance();
    private LiquidURI poolURI;
    private VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();
    private Element sharethisElement;


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


    private void refresh() {

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
        contentArea.clear();
        bus.send(new RetrievePoolRequest(poolURI, true, false), new AbstractResponseCallback<RetrievePoolRequest>() {

            @Override
            public void onFailure(RetrievePoolRequest message, RetrievePoolRequest response) {
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
            public void onSuccess(RetrievePoolRequest message, final RetrievePoolRequest response) {
                final LSDEntity responseEntity = response.getResponse();
                if (responseEntity == null || responseEntity.canBe(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    Window.alert("Why not sign up to create new boards?");
                } else if (responseEntity.canBe(LSDDictionaryTypes.POOL)) {
                    bind(responseEntity.copy());
                } else {
                    Window.alert(responseEntity.getAttribute(LSDAttribute.TITLE));
                }

            }
        });
    }

    private void update(LiquidRequest response) {
        bind(response.getResponse().copy());
    }

    @Override
    protected void onChange(LSDEntity entity) {
        addStyleName("readonly");
        addStyleName("loading");
        final String boardTitle = getEntity().getAttribute(LSDAttribute.TITLE);
        Window.setTitle("Boardcast : " + boardTitle);
        contentArea.init(getEntity(), FormatUtil.getInstance(), threadSafeExecutor);
        StartupUtil.showLiveVersion(getWidget().getElement().getParentElement());
        WidgetUtil.showGracefully(getWidget(), false);
        removeStyleName("loading");


    }


    @UiField
    PoolContentArea contentArea;


    @Override
    protected void onAttach() {
        super.onAttach();

    }

    public SnapshotBoard(boolean embedded) {
        initWidget(ourUiBinder.createAndBindUi(this));
        WidgetUtil.hide(getWidget(), false);
        if (embedded) {
            addStyleName("embedded-board");
        }

    }


}