/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.widgets;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.Permission;
import cazcade.liquid.api.PermissionScope;
import cazcade.liquid.api.lsd.CollectionCallback;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Type;
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

import static cazcade.liquid.api.lsd.Dictionary.*;
import static com.google.gwt.http.client.URL.encode;

/**
 * @author neilellis@cazcade.com
 */
public class PoolContentArea extends Composite {
    interface PoolContentAreaUiBinder extends UiBinder<HTMLPanel, PoolContentArea> {}

    public static final  int                     DEFAULT_WIDTH            = 1024;
    public static final  String                  DEFAULT_BACKGROUND_IMAGE = "/_static/_background/misc/corkboard.jpg";
    private static final PoolContentAreaUiBinder ourUiBinder              = GWT.create(PoolContentAreaUiBinder.class);
    @Nonnull
    private final Bus                      bus;
    @Nullable
    private final VortexScrollPanel        scrollPanel;
    private final boolean                  pageFlow;
    @UiField      AbsolutePanel            container;
    @UiField      Label                    status;
    @Nullable
    private       VortexThreadSafeExecutor threadSafeExecutor;
    @Nullable
    private       PoolPresenter            poolPresenter;

    public PoolContentArea() {
        this(false, false, true);
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
        bus = BusFactory.get();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    public void init(final LiquidURI uri, final FormatUtil features, @Nullable final VortexThreadSafeExecutor threadSafeExecutor, @Nonnull final Type type, final boolean listed) {
        this.threadSafeExecutor = threadSafeExecutor;
        bus.send(new VisitPoolRequest(type, uri, uri, true, listed), new AbstractResponseCallback<VisitPoolRequest>() {
            @Override
            public void onSuccess(final VisitPoolRequest message, @Nonnull final VisitPoolRequest response) {
                ClientLog.log("Got response.");
                final TransferEntity poolEntity = response.response().$();
                ClientLog.log(poolEntity.dump());

                init(poolEntity, threadSafeExecutor);
            }
        });
    }

    public void init(@Nonnull final TransferEntity poolEntity, final VortexThreadSafeExecutor threadSafeExecutor) {
        setBackgroundImage(poolEntity.has$(BACKGROUND_URL) ? poolEntity.$(BACKGROUND_URL) : DEFAULT_BACKGROUND_IMAGE);
        //        backgroundImage.setWidth("100%");
        //        backgroundImage.setHeight("100%");
        //        container.add(backgroundImage);
        if (poolPresenter != null) {
            poolPresenter.destroy();
        }
        final boolean listed = poolEntity.default$bool(LISTED, false);
        status.removeStyleName("danger");
        status.removeStyleName("warning");
        if (poolEntity.default$bool(EDITABLE, false)) {
            if (poolEntity.allowed(PermissionScope.WORLD_SCOPE, Permission.EDIT_PERM)) {
                if (listed) {
                    status.setText("All can edit");
                    status.addStyleName("danger");
                } else {
                    status.setText("Invitees can edit");
                }
            } else if (poolEntity.allowed(PermissionScope.WORLD_SCOPE, Permission.MODIFY_PERM)) {
                if (listed) {
                    status.setText("Everyone can modify");
                    status.addStyleName("warning");
                } else {
                    status.setText("Invitees can modify");
                }
            } else if (poolEntity.allowed(PermissionScope.WORLD_SCOPE, Permission.VIEW_PERM)) {
                if (listed) {
                    status.setText("Everyone can view");
                } else {
                    status.setText("Invitees can view");
                }
            } else {
                if (listed) {
                    status.setText("Listed but not visible");
                    status.addStyleName("warning");
                } else {
                    status.setText("Only you can view");
                }
            }
            WidgetUtil.show(status);
        } else {
            WidgetUtil.hideGracefully(status, false);
        }
        clear();
        poolPresenter = new PoolPresenterImpl(scrollPanel, container, poolEntity, pageFlow, threadSafeExecutor);
        poolPresenter.showInitMode();
        poolEntity.children().each(new CollectionCallback<TransferEntity>() {
            @Override public void call(TransferEntity entity) {
                try {
                    ClientLog.log(entity.type());
                    final PoolObjectPresenter poolObjectPresenter = PoolObjectPresenterFactory.getPresenterForEntity(poolPresenter, entity, threadSafeExecutor);
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
        });
    }

    public void setBackgroundImage(@Nullable final String imageUrl) {
        if (container != null && imageUrl != null) {
            //            Window.alert("setting background "+imageUrl);
            if (BrowserUtil.isInternalImage(imageUrl)) {
                container.getElement()
                         .getStyle()
                         .setProperty("backgroundImage", "url('" + BrowserUtil.convertRelativeUrlToAbsolute(imageUrl) + "')");
            } else {
                container.getElement().getStyle().setProperty("backgroundImage", "url('./_website-snapshot?url=" +
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

    public void center() {
        scrollPanel.center();
    }

}