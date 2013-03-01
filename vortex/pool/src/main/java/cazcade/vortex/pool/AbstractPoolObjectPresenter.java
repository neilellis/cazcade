/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.MessageState;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.bus.client.BusListener;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditFinishHandler;
import cazcade.vortex.common.client.events.EditStartEvent;
import cazcade.vortex.common.client.events.EditStartHandler;
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

import static cazcade.liquid.api.lsd.Dictionary.*;


/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectPresenter<T extends PoolObjectView> implements PoolObjectPresenter<T>, GestureControllable, EndDragHandler, DragHandler, HoldDragHandler {

    private static final int         SNAP_BORDER_X = 20;
    private static final int         SNAP_BORDER_Y = 20;
    protected final      BrowserUtil browserUtil   = GWT.create(BrowserUtil.class);
    protected final VortexThreadSafeExecutor    executor;
    private final   T                           objectView;
    private final   PoolPresenter               pool;
    protected       TransferEntity              entity;
    private         boolean                     locked;
    private         EventBasedGestureController gestureController;
    @Nullable
    private         Widget                      clone;
    private         long                        listenerId;
    private         boolean                     modifiable;
    private         double                      x;
    private         double                      y;
    private         double                      oldX;
    private         double                      oldY;
    private         double                      deltaX;
    private         double                      deltaY;

    public AbstractPoolObjectPresenter(final PoolPresenter pool, final TransferEntity entity, final T objectView, final VortexThreadSafeExecutor executor) {
        this.pool = pool;
        this.entity = entity;
        this.objectView = objectView;
        objectView.getElement().setId(entity.id().toString());
        this.executor = executor;
        update(entity, true);
    }

    private void positionToView(@Nonnull final PoolPresenter pool, @Nonnull final Entity view, @Nonnull final T widget, int count) {
        widget.setLogicalWidth(view.has(VIEW_WIDTH) ? view.$i(VIEW_WIDTH) : getDefaultWidth());
        widget.setLogicalHeight(view.has(VIEW_HEIGHT) ? view.$i(VIEW_HEIGHT) : getDefaultHeight());

        x = view.$d(VIEW_X);
        y = view.$d(VIEW_Y);

        oldX = x;
        oldY = y;
        pool.move(this, x, y, false);
        setZIndexAccordingToPoolOrder(view, widget, count);
    }

    private void setZIndexAccordingToPoolOrder(Entity view, T widget, int count) {
        widget.getElement()
              .getStyle()
              .setZIndex(view.has(VIEW_Z) ? view.$d(VIEW_Z).intValue() : widget.getDefaultZIndex() + count);
    }

    protected int getDefaultHeight() {
        return 200;
    }

    protected int getDefaultWidth() {
        return 300;
    }

    public TransferEntity entity() {
        return entity;
    }

    public T view() {
        return objectView;
    }

    public void select() {
        //        DOM.setStyleAttribute(widget.getElement(), "border", "green 2px solid");
    }

    public void onAddToPool(final int count) {
        if (objectView.getParent() == null) {
            throw new RuntimeException("Cannot add pool object to pool with a widget parent of null.");
        }
        final Entity viewEntity = entity.child(VIEW_ENTITY, true);
        browserUtil.initDraggable(objectView);
        gestureController = new EventBasedGestureController(this, objectView.getInnerWidget(), true, true);
        gestureController.setActive(pool.isModifiable());
        objectView.addHandler(new EditStartHandler() {
            @Override
            public void onEditStart(final EditStartEvent event) {
                objectView.getElement().getStyle().setZIndex(Integer.MAX_VALUE);

            }
        }, EditStartEvent.TYPE);

        objectView.addHandler(new EditFinishHandler() {
            @Override
            public void onEditFinish(final EditFinishEvent event) {
                setZIndexAccordingToPoolOrder(viewEntity, objectView, count);
            }
        }, EditFinishEvent.TYPE);

        objectView.initHandlers(gestureController);
        objectView.addDragHandler(this);
        objectView.addHoldDragHandler(this);
        objectView.addEndDragHandler(this);

        if (viewEntity.has(THEME)) { objectView.setStyleTheme(viewEntity.$(THEME)); }
        if (viewEntity.has(SIZE)) { objectView.setStyleSize(viewEntity.$(SIZE)); }

        //This must be called after theme and size are set but before positionToView
        objectView.onAddToPool();


        listenerId = Bus.get().listen(entity.uri(), new PoolObjectPresenterBusAdapter());


        //This must be called last as all size changes will have been confirmed.
        positionToView(pool, viewEntity, objectView, count);

    }

    @Override
    public void onRemoveFromPool() {
        Bus.get().remove(listenerId);
    }

    @Override
    public void setOnDelete(final Runnable runnable) {
        objectView.setOnDelete(runnable);
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
        return x + objectView.getOffsetWidth();
    }

    @Override
    public double getTop() {
        return y;
    }

    @Override
    public double getBottom() {
        return y + objectView.getOffsetHeight();
    }

    @Override
    public void hide() {
        objectView.setVisible(false);
    }

    @Override
    public void setY(final double y) {
        this.y = y;
    }

    @Override
    public void setX(final double x) {
        this.x = x;
    }

    @Nullable
    public LiquidMessage handle(@Nonnull final UpdatePoolObjectRequest message) {
        final TransferEntity response = message.response();
        if (response != null) {
            update(response, true);
        } else if (message.request() != null) {
            //only a provisional change, so we don't change the underlying entity just it's view.
            update(message.request(), false);
        }
        return null;
    }

    protected void update(final TransferEntity newEntity, final boolean replaceEntity) {
        if (replaceEntity) {
            entity = newEntity;
        }
        updateAccessInformation();

    }

    @Nullable
    public LiquidMessage handle(@Nonnull final MovePoolObjectRequest request) {
        oldX = x;
        oldY = y;
        browserUtil.translateXY(objectView, 0, 0, 0);
        pool.move(this, request.x(), request.y(), false);
        return null;
    }

    @Nullable
    public LiquidMessage handle(@Nonnull final ResizePoolObjectRequest request) {
        browserUtil.resize(objectView, request.width(), request.height(), 100);
        return null;
    }

    @Nullable
    public LiquidMessage handle(@Nonnull final RotateXYPoolObjectRequest request) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                browserUtil.rotate(objectView, request.angle(), 100);
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
            DOM.setStyleAttribute(objectView.getElement(), "opacity", "0.0");
            RootPanel.get().add(clone);
            clone.getElement().setInnerHTML(objectView.getElement().getInnerHTML());
            DOM.setStyleAttribute(clone.getElement(), "position", "absolute");
            DOM.setStyleAttribute(clone.getElement(), "left", objectView.getPageX() + "px");
            DOM.setStyleAttribute(clone.getElement(), "top", objectView.getPageY() + "px");
            DOM.setStyleAttribute(clone.getElement(), "width", objectView.getLogicalWidth() + "px");
            DOM.setStyleAttribute(clone.getElement(), "height", objectView.getLogicalHeight() + "px");
            DOM.setStyleAttribute(clone.getElement(), "zIndex", String.valueOf(Integer.MAX_VALUE));
            clone.getElement().setAttribute("class", objectView.getElement().getAttribute("class"));
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
        DOM.setStyleAttribute(objectView.getElement(), "opacity", "1");
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
            if (!objectView.getStyleName().contains("dragging")) {
                objectView.addStyleName("dragging");
            }
            browserUtil.translateXY(objectView, deltaX, deltaY, 0);
            pool.showDragMode();
        }
    }

    private void constrainMovement() {
        final int offsetWidth = objectView.getOffsetWidth();
        final int offsetHeight = objectView.getOffsetHeight();
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
            if (!objectView.getStyleName().contains("dragging")) {
                objectView.addStyleName("dragging");
            }
            browserUtil.translateXY(objectView, deltaX, deltaY, 0);
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
        objectView.addStyleName("dragging");
        pool.move(this, x, y, true);
        browserUtil.translateXY(objectView, 0, 0, 0);
        pool.move(this, x, y, false);
        new Timer() {
            @Override
            public void run() {
                objectView.removeStyleName("dragging");
            }
        }.schedule(1000);
        pool.hideDragMode();

    }

    public boolean hasCapture() {
        return DOM.getCaptureElement().equals(objectView.getElement());
    }

    public void startCapture() {
        DOM.setCapture(objectView.getElement());
    }

    public void releaseCapture() {
        DOM.releaseCapture(objectView.getElement());
        if (clone != null) {
            DOM.releaseCapture(clone.getElement());
        }
    }

    public void fireEvent(final GwtEvent<?> event) {
        objectView.fireEvent(event);
    }

    @Override
    public double getLeftBounds() {
        return 0;
    }

    @Override
    public double getRightBounds() {
        return objectView.getOffsetWidth();
    }

    @Override
    public double getBottomBounds() {
        return objectView.getOffsetHeight();
    }

    @Override
    public double getTopBounds() {
        return 0;
    }

    @Override
    public Element getBoundingElement() {
        return objectView.getElement();
    }

    protected void updateAccessInformation() {
        if (entity.has(EDITABLE)) {
            view().setEditable(entity.$bool(EDITABLE));
        }
        modifiable = entity.has(MODIFIABLE) && entity.$bool(MODIFIABLE);

    }

    public PoolPresenter getPool() {
        return pool;
    }

    public BusService getBus() {
        return Bus.get();
    }

    public BrowserUtil getBrowserUtil() {
        return browserUtil;
    }

    private class PoolObjectPresenterBusAdapter implements BusListener {
        public void handle(@Nonnull final LiquidMessage message) {
            ClientLog.log("Received message with id of " + message.id());
            final MessageState state = message.state();
            if (state == MessageState.SUCCESS && message instanceof LiquidRequest) {
                final LiquidRequest response = (LiquidRequest) message;
                ClientLog.log("** Successful **  PoolObject message "
                              + response.requestType()
                              + " affecting "
                              + response.affectedEntities()
                              + " and sent to locations "
                              + response.notificationLocations());
                handleRequestInternal(response);
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid green");
            } else if (state == MessageState.DEFERRED && message instanceof LiquidRequest) {
                ClientLog.log("Message with state " + state + " ignored.");
                //                LiquidRequest request = (LiquidRequest) message;
                //                handleRequestInternal(request);
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid orange");
            } else if (state == MessageState.PROVISIONAL && message instanceof LiquidRequest) {
                final LiquidRequest request = (LiquidRequest) message;
                handleRequestInternal(request);
                ClientLog.log("** Provisional ** PoolObject message "
                              + request.requestType()
                              + " affecting "
                              + request.affectedEntities());
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid yellow");
            } else if (state == MessageState.FAIL && message instanceof LiquidRequest) {
                ClientLog.log("Message FAILED.");
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid red");
            } else if (state == MessageState.INITIAL) {
                ClientLog.log("Message with state " + state + " ignored.");
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid white");
            } else {
                ClientLog.log("Message with state " + state + " ignored.");
                //                DOM.setStyleAttribute(widget.getElement(), "border", "1px solid blue");
            }
        }
    }


}
