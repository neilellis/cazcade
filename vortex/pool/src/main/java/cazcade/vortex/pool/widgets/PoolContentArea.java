package cazcade.vortex.pool.widgets;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDType;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PoolContentArea extends Composite {
    public static final int DEFAULT_WIDTH = 1024;

    private static PoolContentAreaUiBinder ourUiBinder = GWT.create(PoolContentAreaUiBinder.class);

    @UiField
    AbsolutePanel container;

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
                LSDEntity poolEntity = response.getResponse();
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
        poolPresenter = new PoolPresenterImpl(scrollPanel, container, poolEntity, pageFlow, features, threadSafeExecutor);
        List<LSDEntity> entities = poolEntity.getSubEntities(LSDAttribute.CHILD);
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
            }
        }
    }

    public void setBackgroundImage(String imageUrl) {
        if (container != null && imageUrl != null) {
//            Window.alert("setting background "+imageUrl);
            container.getElement().getStyle().setProperty("backgroundImage", "url('./_image-service?url=" + encode(imageUrl) + "&size=CLIPPED_LARGE')");
            container.getElement().getStyle().setWidth(1024, Style.Unit.PX);
        }
    }


    public void clear() {
        for (Widget widget : container) {
            try {
                if (!(widget instanceof Image)) {
                    widget.removeFromParent();
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
        scrollPanel.scrollToTopLeft();

    }


    interface PoolContentAreaUiBinder extends UiBinder<HTMLPanel, PoolContentArea> {
    }

    public void center() {
        scrollPanel.center();
    }

}