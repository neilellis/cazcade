/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.widgets;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;

import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PoolContentArea extends Composite {
    public static final int DEFAULT_WIDTH = 1024;

    private static final PoolContentAreaUiBinder ourUiBinder = GWT.create(PoolContentAreaUiBinder.class);

    @UiField AbsolutePanel container;
    @UiField Label         visibilityStatus;

    @Nonnull
    private final Bus bus;

    @Nullable
    private final VortexScrollPanel        scrollPanel;
    @Nullable
    private       VortexThreadSafeExecutor threadSafeExecutor;
    @Nullable
    private       PoolPresenter            poolPresenter;
    private final boolean                  pageFlow;

    public PoolContentArea() {
        this(false, false, true);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @UiConstructor
    public PoolContentArea(final boolean scrollX, final boolean scrollY, final boolean pageFlow) {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        this.pageFlow = pageFlow;
        scrollPanel = new VortexScrollPanel(widget, scrollX, scrollY, true, pageFlow, null);
        initWidget(scrollPanel);
        if (!pageFlow) {
            setHeight("100%");
        }
        bus = BusFactory.getInstance();
    }


    public void init(final LiquidURI uri, final FormatUtil features, @Nullable final VortexThreadSafeExecutor threadSafeExecutor, @Nonnull final LSDType type, final boolean listed) {
        this.threadSafeExecutor = threadSafeExecutor;
        bus.send(new VisitPoolRequest(type, uri, uri, true, listed), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override
            public void onSuccess(final VisitPoolRequest message, @Nonnull final VisitPoolRequest response) {
                ClientLog.log("Got response.");
                final LSDTransferEntity poolEntity = response.getResponse().copy();
                ClientLog.log(poolEntity.dump());

                init(poolEntity, features, threadSafeExecutor);
            }
        });
    }

    public void init(@Nonnull final LSDTransferEntity poolEntity, final FormatUtil features, final VortexThreadSafeExecutor threadSafeExecutor) {
        if (poolEntity.hasAttribute(LSDAttribute.IMAGE_URL)) {
            final String imageUrl = poolEntity.getAttribute(LSDAttribute.IMAGE_URL);
            setBackgroundImage(imageUrl);
        }
        //        backgroundImage.setWidth("100%");
        //        backgroundImage.setHeight("100%");
        //        container.add(backgroundImage);
        if (poolPresenter != null) {
            poolPresenter.destroy();
        }
        final boolean listed = poolEntity.getBooleanAttribute(LSDAttribute.LISTED, false);
        visibilityStatus.removeStyleName("danger");
        visibilityStatus.removeStyleName("warning");
        if (poolEntity.getBooleanAttribute(LSDAttribute.EDITABLE, false)) {
            if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT)) {
                if (listed) {
                    visibilityStatus.setText("All can edit");
                    visibilityStatus.addStyleName("danger");
                }
                else {
                    visibilityStatus.setText("Invitees can edit");
                }
            }
            else if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                if (listed) {
                    visibilityStatus.setText("Everyone can modify");
                    visibilityStatus.addStyleName("warning");
                }
                else {
                    visibilityStatus.setText("Invitees can modify");
                }
            }
            else if (poolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
                if (listed) {
                    visibilityStatus.setText("Everyone can view");
                }
                else {
                    visibilityStatus.setText("Invitees can view");
                }
            }
            else {
                if (listed) {
                    visibilityStatus.setText("Listed but not visible");
                    visibilityStatus.addStyleName("warning");
                }
                else {
                    visibilityStatus.setText("Only you can view");
                }
            }
            WidgetUtil.show(visibilityStatus);
        }
        else {
            WidgetUtil.hideGracefully(visibilityStatus, false);
        }
        clear();
        poolPresenter = new PoolPresenterImpl(scrollPanel, container, poolEntity, pageFlow, features, threadSafeExecutor);
        poolPresenter.showInitMode();
        final HashSet<LSDBaseEntity> entities = new HashSet<LSDBaseEntity>(poolEntity.getSubEntities(LSDAttribute.CHILD));
        for (final LSDBaseEntity entity : entities) {
            try {
                ClientLog.log(entity.getTypeDef().asString());
                final PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(poolPresenter, (LSDTransferEntity) entity, features, threadSafeExecutor);
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

    public void setBackgroundImage(@Nullable final String imageUrl) {
        if (container != null && imageUrl != null) {
            //            Window.alert("setting background "+imageUrl);
            if (BrowserUtil.isInternalImage(imageUrl)) {
                container.getElement().getStyle().setProperty("backgroundImage", "url('" + imageUrl + "')");
            }
            else {
                container.getElement().getStyle().setProperty("backgroundImage", "url('./_image-service?url=" +
                                                                                 encode(imageUrl) +
                                                                                 "&size=LARGE&width=1024&height=2048')");
            }
            container.getElement().getStyle().setWidth(1024, Style.Unit.PX);
        }
    }


    public void clear() {
        WidgetUtil.removeAllChildren(container);
        scrollPanel.scrollToTopLeft();

    }


    interface PoolContentAreaUiBinder extends UiBinder<HTMLPanel, PoolContentArea> {}

    public void center() {
        scrollPanel.center();
    }

}