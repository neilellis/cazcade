package cazcade.vortex.pool.widgets;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDType;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.PoolPresenterImpl;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenterFactory;
import cazcade.vortex.widgets.client.panels.scroll.VortexScrollPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.HashSet;
import java.util.Set;

import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PoolContentArea extends Composite {
    public static final int DEFAULT_WIDTH = 1024;

    private static PoolContentAreaUiBinder ourUiBinder = GWT.create(PoolContentAreaUiBinder.class);

    @UiField
    AbsolutePanel container;
    @UiField
    Label visibilityRibbon;

    private Bus bus;

    private VortexScrollPanel scrollPanel;
    private VortexThreadSafeExecutor threadSafeExecutor;
    private PoolPresenter poolPresenter;
    private boolean pageFlow;

    public PoolContentArea() {
        this(false, false);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @UiConstructor
    public PoolContentArea(boolean scrollX, boolean scrollY) {
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        scrollPanel = new VortexScrollPanel(widget, scrollX, scrollY, true, null);
        initWidget(scrollPanel);
        bus = BusFactory.getInstance();
        pageFlow = true;
    }


    public void init(final LiquidURI uri, final FormatUtil features, final VortexThreadSafeExecutor threadSafeExecutor, LSDType type, boolean listed) {
        this.threadSafeExecutor = threadSafeExecutor;
        bus.send(new VisitPoolRequest(type, uri, uri, true, listed), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override
            public void onSuccess(VisitPoolRequest message, final VisitPoolRequest response) {
                ClientLog.log("Got response.");
                LSDEntity poolEntity = response.getResponse().copy();
                ClientLog.log(poolEntity.dump());

                PoolContentArea.this.init(poolEntity, features, threadSafeExecutor);
            }
        });
    }

    public void init(LSDEntity poolEntity, FormatUtil features, VortexThreadSafeExecutor threadSafeExecutor) {
        final String imageUrl = poolEntity.getAttribute(LSDAttribute.IMAGE_URL);
        setBackgroundImage(imageUrl);
//        backgroundImage.setWidth("100%");
//        backgroundImage.setHeight("100%");
//        container.add(backgroundImage);
        if (poolPresenter != null) {
            poolPresenter.destroy();
        }
        final boolean listed = poolEntity.getBooleanAttribute(LSDAttribute.LISTED);
        visibilityRibbon.removeStyleName("danger");
        visibilityRibbon.removeStyleName("warning");
        if (poolEntity.getBooleanAttribute(LSDAttribute.EDITABLE)) {
            if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT)) {
                if (listed) {
                    visibilityRibbon.setText("All can edit");
                    visibilityRibbon.addStyleName("danger");
                } else {
                    visibilityRibbon.setText("Invitees can edit");
                }
            } else if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                if (listed) {
                    visibilityRibbon.setText("Everyone can modify");
                    visibilityRibbon.addStyleName("warning");
                } else {
                    visibilityRibbon.setText("Invitees can modify");
                }
            } else if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
                if (listed) {
                    visibilityRibbon.setText("Everyone can view");
                } else {
                    visibilityRibbon.setText("Invitees can view");
                }
            } else {
                if (listed) {
                    visibilityRibbon.setText("Listed but not visible");
                    visibilityRibbon.addStyleName("warning");
                } else {
                    visibilityRibbon.setText("Only you can view");
                }
            }
            WidgetUtil.show(visibilityRibbon);
        } else {
            WidgetUtil.hideGracefully(visibilityRibbon, false);
        }
        clear();
        poolPresenter = new PoolPresenterImpl(scrollPanel, container, poolEntity, pageFlow, features, threadSafeExecutor);
        poolPresenter.showInitMode();
        Set<LSDEntity> entities = new HashSet<LSDEntity>(poolEntity.getSubEntities(LSDAttribute.CHILD));
        for (LSDEntity entity : entities) {
            try {
                ClientLog.log(entity.getTypeDef().asString());
                PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(poolPresenter, entity, features, threadSafeExecutor);
                if (poolObjectPresenter != null) {
                    ClientLog.assertTrue(poolObjectPresenter != null, "Pool Object Presenter was null");
                    poolObjectPresenter.setX(scrollPanel.getOffsetX() + 200);
                    poolObjectPresenter.setY(scrollPanel.getOffsetY() + 200);
                    poolPresenter.add(poolObjectPresenter);
                }

            } catch (Throwable e) {
                ClientLog.log(e);
            } finally {
                new Timer() {
                    @Override
                    public void run() {
                        poolPresenter.hideInitMode();
                    }
                }.schedule(500);
            }
        }
    }

    public void setBackgroundImage(String imageUrl) {
        if (container != null && imageUrl != null) {
//            Window.alert("setting background "+imageUrl);
            if (BrowserUtil.isInternalImage(imageUrl)) {
                container.getElement().getStyle().setProperty("backgroundImage", "url('" + imageUrl + "')");
            } else {
                container.getElement().getStyle().setProperty("backgroundImage", "url('./_image-service?url=" + encode(imageUrl) + "&size=LARGE&width=1024&height=2048')");
            }
            container.getElement().getStyle().setWidth(1024, Style.Unit.PX);
        }
    }


    public void clear() {
        WidgetUtil.removeAllChildren(container);
        scrollPanel.scrollToTopLeft();

    }


    interface PoolContentAreaUiBinder extends UiBinder<HTMLPanel, PoolContentArea> {
    }

    public void center() {
        scrollPanel.center();
    }

}