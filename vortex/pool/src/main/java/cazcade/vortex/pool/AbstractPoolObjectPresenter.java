package cazcade.vortex.pool;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.dnd.client.gesture.EventBasedGestureController;
import cazcade.vortex.dnd.client.gesture.GestureControllable;
import cazcade.vortex.dnd.client.gesture.drag.DragEvent;
import cazcade.vortex.dnd.client.gesture.drag.DragHandler;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragEvent;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragHandler;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragEvent;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragHandler;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectPresenter<T extends PoolObjectView> implements PoolObjectPresenter<T>, GestureControllable, EndDragHandler, DragHandler, HoldDragHandler {

    protected final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    protected Bus bus;
    protected LSDTransferEntity entity;
    private final T poolObjectView;
    protected final VortexThreadSafeExecutor threadSafeExecutor;
    private final PoolPresenter pool;
    private boolean locked;
    private EventBasedGestureController gestureController;
    @Nullable
    private Widget clone;
    private long listenerId;
    private boolean modifiable;
    private double x;
    private double y;
    private double oldX;
    private double oldY;
    private double deltaX;
    private double deltaY;
    private static final int SNAP_BORDER_X = 20;
    private static final int SNAP_BORDER_Y = 20;

    public AbstractPoolObjectPresenter(final PoolPresenter pool, final LSDTransferEntity entity, final T poolObjectView, final VortexThreadSafeExecutor threadSafeExecutor) {
        this.pool = pool;
        this.entity = entity;
        this.poolObjectView = poolObjectView;
        this.threadSafeExecutor = threadSafeExecutor;
        update(entity, true);
    }

    private void positionToView(@Nonnull final PoolPresenter pool, @Nonnull final LSDBaseEntity viewEntity, @Nonnull final T widget) {
        if (viewEntity.hasAttribute(LSDAttribute.VIEW_WIDTH)) {
            widget.setLogicalWidth(Integer.parseInt(viewEntity.getAttribute(LSDAttribute.VIEW_WIDTH)));
        } else {
            widget.setLogicalWidth(getDefaultWidth());
        }
        if (viewEntity.hasAttribute(LSDAttribute.VIEW_HEIGHT)) {
            widget.setLogicalHeight(Integer.parseInt(viewEntity.getAttribute(LSDAttribute.VIEW_HEIGHT)));
        } else {
            widget.setLogicalHeight(getDefaultHeight());
        }

        x = Double.parseDouble(viewEntity.getAttribute(LSDAttribute.VIEW_X));
        y = Double.parseDouble(viewEntity.getAttribute(LSDAttribute.VIEW_Y));
        oldX = x;
        oldY = y;
        pool.move(this, x, y, false);
//        DOM.setStyleAttribute(widget.getElement(), "position", "relative");
//        DOM.setStyleAttribute(widget.getElement(), "left", (x + (pool.getWidth() / 2) - widget.getOffsetWidth() / 2) + "px");
//        DOM.setStyleAttribute(widget.getElement(), "top", (y + (pool.getHeight() / 2) - widget.getOffsetHeight() / 2) + "px");
//        ClientLog.log("left " + (x + (pool.getWidth() / 2) - widget.getOffsetWidth() / 2) + "px");
//        ClientLog.log("top " + (y + (pool.getHeight() / 2) - widget.getOffsetHeight() / 2) + "px");
    }

    protected int getDefaultHeight() {
        return 200;
    }

    protected int getDefaultWidth() {
        return 300;
    }

    public LSDTransferEntity getEntity() {
        return entity;
    }

    public T getPoolObjectView() {
        return poolObjectView;
    }

    public void select() {
//        DOM.setStyleAttribute(widget.getElement(), "border", "green 2px solid");
    }

    public void onAddToPool() {
        if (poolObjectView.getParent() == null) {
            throw new RuntimeException("Cannot add pool object to pool with a widget parent of null.");
        }
        final LSDBaseEntity viewEntity = entity.getSubEntity(LSDAttribute.VIEW, true);
        browserUtil.initDraggable(poolObjectView);
        gestureController = new EventBasedGestureController(this, poolObjectView.getInnerWidget(), true, true);

        poolObjectView.addHandler(new EditStartHandler() {
            @Override
            public void onEditStart(final EditStart event) {
                //gestureController.setActive(false);
            }
        }, EditStart.TYPE);

        poolObjectView.addHandler(new EditFinishHandler() {
            @Override
            public void onEditFinish(final EditFinish event) {
                //gestureController.setActive(true);
            }
        }, EditFinish.TYPE);

        poolObjectView.initHandlers(gestureController);
        poolObjectView.addDragHandler(this);
        poolObjectView.addHoldDragHandler(this);
        poolObjectView.addEndDragHandler(this);

        if (viewEntity.hasAttribute(LSDAttribute.THEME)) {
            getPoolObjectView().setStyleTheme(viewEntity.getAttribute(LSDAttribute.THEME));
        }
        if (viewEntity.hasAttribute(LSDAttribute.SIZE)) {
            getPoolObjectView().setStyleSize(viewEntity.getAttribute(LSDAttribute.SIZE));

        }
        //This must be called after theme and size are set but before positionToView
        poolObjectView.onAddToPool();


        bus = BusFactory.getInstance();
        listenerId = bus.listenForURI(entity.getURI(), new PoolObjectPresenterBusAdapter());


        //This must be called last as all size changes will have been confirmed.
        positionToView(pool, viewEntity, poolObjectView);

    }

    @Override
    public void onRemoveFromPool() {
        bus.removeListener(listenerId);
    }

    @Override
    public void setOnDelete(final Runnable runnable) {
        poolObjectView.setOnDelete(runnable);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getLeft() {
        return x;
    }

    @Override
    public double getRight() {
        return x + poolObjectView.getOffsetWidth();
    }

    @Override
    public double getTop() {
        return y;
    }

    @Override
    public double getBottom() {
        return y + poolObjectView.getOffsetHeight();
    }

    @Override
    public void hide() {
        poolObjectView.setVisible(false);
    }


    @Nullable
    public LiquidMessage handle(@Nonnull final UpdatePoolObjectRequest request) {
        final LSDTransferEntity responseEntity = request.getResponse();
        if (responseEntity != null) {
            update(responseEntity, true);
        } else if (request.getRequestEntity() != null) {
            //only a provisional change, so we don't change the underlying entity just it's view.
            update(request.getRequestEntity(), false);
        }
        return null;
    }

    protected void update(final LSDTransferEntity newEntity, final boolean replaceEntity) {
        if (replaceEntity) {
            entity = newEntity;
        }
        updateAccessInformation();

    }

    @Nullable
    public LiquidMessage handle(@Nonnull final MovePoolObjectRequest request) {
        oldX = x;
        oldY = y;
        browserUtil.translateXY(poolObjectView, 0, 0, 0);
        pool.move(this, request.getX(), request.getY(), false);
        return null;
    }

    @Nullable
    public LiquidMessage handle(@Nonnull final ResizePoolObjectRequest request) {
        browserUtil.resize(poolObjectView, request.getWidth(), request.getHeight(), 100);
        return null;
    }

    @Nullable
    public LiquidMessage handle(@Nonnull final RotateXYPoolObjectRequest request) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                browserUtil.rotate(poolObjectView, request.getAngle(), 100);
            }
        });
        return null;
    }

    public void handleRequestInternal(final LiquidRequest request) {
        gestureController.reset();
        if (request instanceof MovePoolObjectRequest) {
            handle((MovePoolObjectRequest) request);
        } else if (request instanceof ResizePoolObjectRequest) {
            handle((ResizePoolObjectRequest) request);
        } else if (request instanceof RotateXYPoolObjectRequest) {
            handle((RotateXYPoolObjectRequest) request);
        } else if (request instanceof UpdatePoolObjectRequest) {
            handle((UpdatePoolObjectRequest) request);
        }
    }

    private void startClone() {
        if (clone != null) {
            endClone();
        }
        try {
            clone = new SimplePanel();
            clone.setVisible(false);
            DOM.setStyleAttribute(poolObjectView.getElement(), "opacity", "0.0");
            RootPanel.get().add(clone);
            clone.getElement().setInnerHTML(poolObjectView.getElement().getInnerHTML());
            DOM.setStyleAttribute(clone.getElement(), "position", "absolute");
            DOM.setStyleAttribute(clone.getElement(), "left", poolObjectView.getPageX() + "px");
            DOM.setStyleAttribute(clone.getElement(), "top", poolObjectView.getPageY() + "px");
            DOM.setStyleAttribute(clone.getElement(), "width", poolObjectView.getLogicalWidth() + "px");
            DOM.setStyleAttribute(clone.getElement(), "height", poolObjectView.getLogicalHeight() + "px");
            DOM.setStyleAttribute(clone.getElement(), "zIndex", String.valueOf(Integer.MAX_VALUE));
            clone.getElement().setAttribute("class", poolObjectView.getElement().getAttribute("class"));
            clone.setVisible(true);
            DOM.setStyleAttribute(clone.getElement(), "opacity", "1.0");
//            pool.getDragBoundContainer().add(clone);
        } catch (Exception e) {
            ClientLog.log(e);
            endClone();
        }

    }

    private void endClone() {
        //Here we're trying to keep an overlap between the re-appearance of the original and the
        //removal of the clone - just a visual trick really.
        new Timer() {

            @Override
            public void run() {
                removeClone();
            }
        }.schedule(300);
        DOM.setStyleAttribute(poolObjectView.getElement(), "opacity", "1");
    }

    private void removeClone() {
        if (clone != null) {
            clone.removeFromParent();
            clone = null;
        }
    }

    public void onDrag(@Nonnull final DragEvent dragEvent) {
        if (isDraggable()) {
            deltaX = dragEvent.getDeltaX();
            deltaY = dragEvent.getDeltaY();
            constrainMovement();
//            pool.move(this, x, y);
            if (!poolObjectView.getStyleName().contains("dragging")) {
                poolObjectView.addStyleName("dragging");
            }
            browserUtil.translateXY(poolObjectView, deltaX, deltaY, 0);
            pool.showDragMode();
        }
    }

    private void constrainMovement() {
        final int offsetWidth = poolObjectView.getOffsetWidth();
        final int offsetHeight = poolObjectView.getOffsetHeight();
        if (x + deltaX + offsetWidth > pool.getWidth() - SNAP_BORDER_X) {
            deltaX = pool.getWidth() - offsetWidth - x;
//            deltaX= 0;
        }
        if (x + deltaX < SNAP_BORDER_X) {
            deltaX = -x;
//            deltaX= 0
        }
        if (y + deltaY + offsetHeight > pool.getHeight() - SNAP_BORDER_Y && !pool.isPageFlow()) {
            deltaY = pool.getHeight() - offsetHeight / 2 - y;
//            deltaY= 0;
        }
        if (y + deltaY < SNAP_BORDER_Y) {
            deltaY = -y;
//            deltaY= 0;
        }
    }

    private boolean isDraggable() {
        return modifiable;
    }


    public void onHoldDrag(@Nonnull final HoldDragEvent dragEvent) {
        if (isDraggable()) {
//            if (clone == null) {
//                startClone();
//            }
            deltaX = dragEvent.getDeltaX();
            deltaY = dragEvent.getDeltaY();
            constrainMovement();
            if (!poolObjectView.getStyleName().contains("dragging")) {
                poolObjectView.addStyleName("dragging");
            }
            browserUtil.translateXY(poolObjectView, deltaX, deltaY, 0);
//            pool.move(this, x, y);
            pool.showDragMode();

        }
    }


    public void onEndDrag(final EndDragEvent dragEvent) {
//        int oldLeft= widget.getAbsoluteLeft();
//        int oldTop= widget.getAbsoluteTop();
//        pool.setWidgetLogicalPosition(widget, x, y);
//        releaseCapture();
        x = x + deltaX;
        y = y + deltaY;
        oldX = x;
        oldY = y;
        poolObjectView.addStyleName("dragging");
        pool.move(this, x, y, true);
        browserUtil.translateXY(poolObjectView, 0, 0, 0);
        pool.move(this, x, y, false);
        new Timer() {
            @Override
            public void run() {
                poolObjectView.removeStyleName("dragging");
            }
        }.schedule(1000);
        pool.hideDragMode();

    }


    public boolean hasCapture() {
        return DOM.getCaptureElement().equals(poolObjectView.getElement());
    }

    public void startCapture() {
        DOM.setCapture(poolObjectView.getElement());
    }

    public void releaseCapture() {
        DOM.releaseCapture(poolObjectView.getElement());
        if (clone != null) {
            DOM.releaseCapture(clone.getElement());
        }
    }

    public void fireEvent(final GwtEvent<?> event) {
        poolObjectView.fireEvent(event);
    }

    protected void updateAccessInformation() {
        if (entity.hasAttribute(LSDAttribute.EDITABLE)) {
            getPoolObjectView().setEditable(entity.getBooleanAttribute(LSDAttribute.EDITABLE));
        }
        modifiable = entity.hasAttribute(LSDAttribute.MODIFIABLE) && entity.getBooleanAttribute(LSDAttribute.MODIFIABLE);

    }


    private class PoolObjectPresenterBusAdapter implements BusListener {
        public void handle(@Nonnull final LiquidMessage message) {
            ClientLog.log("Received message with id of " + message.getId());
            final LiquidMessageState state = message.getState();
            if (state == LiquidMessageState.SUCCESS && message instanceof LiquidRequest) {
                final LiquidRequest response = (LiquidRequest) message;
                ClientLog.log("** Successful **  PoolObject message " + response.getRequestType() + " affecting " + response.getAffectedEntities() + " and sent to locations " + response.getNotificationLocations());
                handleRequestInternal(response);
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid green");
            } else if (state == LiquidMessageState.DEFERRED && message instanceof LiquidRequest) {
                ClientLog.log("Message with state " + state + " ignored.");
//                LiquidRequest request = (LiquidRequest) message;
//                handleRequestInternal(request);
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid orange");
            } else if (state == LiquidMessageState.PROVISIONAL && message instanceof LiquidRequest) {
                final LiquidRequest request = (LiquidRequest) message;
                handleRequestInternal(request);
                ClientLog.log("** Provisional ** PoolObject message " + request.getRequestType() + " affecting " + request.getAffectedEntities());
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid yellow");
            } else if (state == LiquidMessageState.FAIL && message instanceof LiquidRequest) {
                ClientLog.log("Message FAILED.");
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid red");
            } else if (state == LiquidMessageState.INITIAL) {
                ClientLog.log("Message with state " + state + " ignored.");
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid white");
            } else {
                ClientLog.log("Message with state " + state + " ignored.");
//                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid blue");
            }
        }
    }

    public PoolPresenter getPool() {
        return pool;
    }

    public Bus getBus() {
        return bus;
    }

    public BrowserUtil getBrowserUtil() {
        return browserUtil;
    }

    @Override
    public void setX(final double x) {
        this.x = x;
    }

    @Override
    public void setY(final double y) {
        this.y = y;
    }

    @Override
    public double getLeftBounds() {
        return 0;
    }

    @Override
    public double getRightBounds() {
        return poolObjectView.getOffsetWidth();
    }

    @Override
    public double getBottomBounds() {
        return poolObjectView.getOffsetHeight();
    }

    @Override
    public double getTopBounds() {
        return 0;
    }

    @Override
    public Element getBoundingElement() {
        return poolObjectView.getElement();
    }


}
