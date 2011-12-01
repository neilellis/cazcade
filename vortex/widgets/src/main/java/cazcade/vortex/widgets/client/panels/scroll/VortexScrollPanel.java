package cazcade.vortex.widgets.client.panels.scroll;

import cazcade.vortex.dnd.client.GestureEvent;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.dnd.client.gesture.EventBasedGestureController;
import cazcade.vortex.dnd.client.gesture.GestureControllable;
import cazcade.vortex.dnd.client.gesture.drag.DragEvent;
import cazcade.vortex.dnd.client.gesture.drag.DragHandler;
import cazcade.vortex.dnd.client.gesture.drag.HasDragHandler;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragEvent;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragHandler;
import cazcade.vortex.dnd.client.gesture.enddrag.HasEndDragHandler;
import cazcade.vortex.dnd.client.gesture.hdrag.HasHoldDragHandler;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragEvent;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragHandler;
import cazcade.vortex.dnd.client.touch.HasAllTouchHandlers;
import cazcade.vortex.dnd.client.touch.cancel.TouchCancelEvent;
import cazcade.vortex.dnd.client.touch.cancel.TouchCancelHandler;
import cazcade.vortex.dnd.client.touch.end.TouchEndEvent;
import cazcade.vortex.dnd.client.touch.end.TouchEndHandler;
import cazcade.vortex.dnd.client.touch.move.TouchMoveEvent;
import cazcade.vortex.dnd.client.touch.move.TouchMoveHandler;
import cazcade.vortex.dnd.client.touch.start.TouchStartEvent;
import cazcade.vortex.dnd.client.touch.start.TouchStartHandler;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public final class VortexScrollPanel extends HTMLPanel implements GestureControllable, HasEndDragHandler, HasHoldDragHandler, HasDragHandler, HasMouseWheelHandlers, DragHandler, HoldDragHandler, EndDragHandler, MouseWheelHandler {
    private final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    private HorizontalScrollBar horizontalScrollBar;
    private VerticalScrollBar verticalScrollBar;
    @Nonnull
    private final Widget inner;
    private final boolean xMovementAllowed;
    private final boolean yMovementAllowed;
    private final boolean startInMiddle;
    private final Runnable onUserAction;
    private int offsetX;
    @Nonnull
    private final ScrollAreaPanel outer;
    private int offsetY;

    public VortexScrollPanel(@Nonnull final Widget inner, final boolean xMovementAllowed, final boolean yMovementAllowed, final boolean startInMiddle, final Runnable onUserAction) {
        this(inner, xMovementAllowed, yMovementAllowed, startInMiddle, false, onUserAction);
    }

    public VortexScrollPanel(@Nonnull final Widget inner, final boolean xMovementAllowed, final boolean yMovementAllowed, final boolean startInMiddle, final boolean pageFlow, final Runnable onUserAction) {
        super("");
        this.inner = inner;
        this.xMovementAllowed = xMovementAllowed;
        this.yMovementAllowed = yMovementAllowed;
        this.startInMiddle = startInMiddle;
        this.onUserAction = onUserAction;

        setWidth("100%");
        addStyleName("drag-scroll-panel");
        DOM.setStyleAttribute(getElement(), "position", "relative");
        outer = new ScrollAreaPanel();
//        DOM.setStyleAttribute(inner.getElement(), "border", "5px solid yellow");
        outer.setWidth("100%");
        setHeightForFlow(pageFlow);
        outer.add(inner);
        outer.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        add(outer);
        if (inner.getElement().getId() == null) {
            Window.alert("Scrollable areas must have ids.");
        }

    }

    private void setHeightForFlow(final boolean pageFlow) {
        if (pageFlow) {
            outer.setHeight("auto");
        } else {
            outer.setHeight("100%");
        }
        if (pageFlow) {
            setHeight("auto");
        } else {
            setHeight("100%");
        }
    }

    public void init() {

    }


    public void center() {
        offsetX = -inner.getOffsetWidth() / 2;
        offsetY = -inner.getOffsetHeight() / 2;
        moveTo(offsetX, offsetY, 0);
    }

    public boolean hasCapture() {
        return DOM.getCaptureElement().equals(outer.getElement());
    }

    public void startCapture() {
        DOM.setCapture(outer.getElement());
    }

    public void releaseCapture() {
        DOM.releaseCapture(outer.getElement());
    }

    @Override
    public double getLeftBounds() {
        return 0;
    }

    @Override
    public double getRightBounds() {
        return getOffsetWidth();
    }

    @Override
    public double getBottomBounds() {
        return getOffsetHeight();
    }

    @Override
    public double getTopBounds() {
        return 0;
    }

    @Override
    public Element getBoundingElement() {
        return getElement();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        horizontalScrollBar = new HorizontalScrollBar(outer, inner);
        horizontalScrollBar.setVisible(xMovementAllowed);
        verticalScrollBar = new VerticalScrollBar(outer, inner);
        verticalScrollBar.setVisible(yMovementAllowed);
        if (xMovementAllowed || yMovementAllowed) {
            addDragHandler(this);
            addHoldDragHandler(this);
            addEndDragHandler(this);
            add(horizontalScrollBar);
            add(verticalScrollBar);
            //Now it's all set up, add the event handler.
            final EventBasedGestureController handler = new EventBasedGestureController(this, inner, xMovementAllowed, yMovementAllowed);
            outer.init(handler);
            getElement().setId((System.currentTimeMillis() + "-" + Math.random()).replace(',', '_'));
            new Timer() {
                @Override
                public void run() {
                    if (startInMiddle) {
                        center();
                    }

                }
            }.schedule(100);
            setScrollHandler(this, getElement().getId());
        }
    }


    public HandlerRegistration addEndDragHandler(final EndDragHandler endDragHandler) {
        return addHandler(endDragHandler, EndDragEvent.getType());
    }

    public HandlerRegistration addDragHandler(final DragHandler dragHandler) {
        return addHandler(dragHandler, DragEvent.getType());
    }

    public HandlerRegistration addHoldDragHandler(final HoldDragHandler dragHandler) {
        return addHandler(dragHandler, HoldDragEvent.getType());
    }

    public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler handler) {
        return addHandler(handler, MouseWheelEvent.getType());
    }

    public void onDrag(@Nonnull final DragEvent dragEvent) {
        onDragInternal(dragEvent);
    }


    private void onDragInternal(@Nonnull final GestureEvent event) {
        userAction();

        int x = 0;
        if (xMovementAllowed) {
            if (offsetX + event.getDeltaX() < offsetLimitX()) {
                if (inner.getOffsetWidth() > offsetLimitX()) {
                    x = offsetLimitX() + (int) ((offsetX + event.getDeltaX() - offsetLimitX()) * 0.2);
                }
            } else if (offsetX + event.getDeltaX() > 0) {
                x = (int) ((offsetX + event.getDeltaX()) * 0.2);
            } else {
                x = offsetX + event.getDeltaX();
            }

        }
        int y = 0;


        if (yMovementAllowed) {
            if (offsetY + event.getDeltaY() < offsetLimitY()) {
                if (inner.getOffsetHeight() > offsetLimitY()) {
                    y = offsetLimitY() + (int) ((offsetY + event.getDeltaY() - offsetLimitY()) * 0.2);
                }
            } else if (offsetY + event.getDeltaY() > 0) {
                y = (int) ((offsetY + event.getDeltaY()) * 0.2);
            } else {
                y = offsetY + event.getDeltaY();
            }

        }

        moveTo(x, y, 50);
    }

    private int offsetLimitY() {
        return -inner.getOffsetHeight() + outer.getOffsetHeight();
    }

    private int offsetLimitX() {
        return -inner.getOffsetWidth() + outer.getOffsetWidth();
    }

    private void moveTo(final int x, final int y, final int speed) {
        horizontalScrollBar.update(x);
        verticalScrollBar.update(y);
        browserUtil.translateXY(inner, x, y, speed);
    }

    public void onHoldDrag(@Nonnull final HoldDragEvent holdDragEvent) {
        onDragInternal(holdDragEvent);
        userAction();

    }

    public void onEndDrag(@Nonnull final EndDragEvent endDragEvent) {
        final int deltaX = endDragEvent.getDeltaX();
        final int deltaY = endDragEvent.getDeltaY();
        ClientLog.log("offset-x= " + offsetX + " and delta-x=" + deltaX);
        moveToDelta(deltaX, deltaY);
        userAction();

    }

    private void moveToDelta(final double deltaX, final double deltaY) {
        if (xMovementAllowed) {
            if (-(offsetX + deltaX) > inner.getOffsetWidth() - outer.getOffsetWidth()) {
                if (inner.getOffsetWidth() > offsetLimitX()) {
                    ClientLog.log("Snapped to end X.");
                    offsetX = -inner.getOffsetWidth() + outer.getOffsetWidth();
                }
            } else if (offsetX + deltaX > 0) {
                ClientLog.log("Snapped to beginning X.");
                offsetX = 0;
            } else if (offsetX + inner.getOffsetWidth() < outer.getOffsetWidth()) {
                offsetX = outer.getOffsetWidth() - inner.getOffsetWidth();
            } else {
                offsetX += deltaX;
            }
        }

        if (yMovementAllowed) {
            if (-(offsetY + deltaY) > inner.getOffsetHeight() - outer.getOffsetHeight()) {
                if (inner.getOffsetHeight() > offsetLimitY()) {
                    offsetY = outer.getOffsetHeight() - inner.getOffsetHeight();
                    ClientLog.log("Snapped to end Y, offsetY=" + offsetY);
                }
            } else if (offsetY + deltaY > 0) {
                ClientLog.log("Snapped back Y.");
                offsetY = 0;
            } else if (offsetY + inner.getOffsetHeight() < outer.getOffsetHeight()) {
                offsetY = outer.getOffsetHeight() - inner.getOffsetHeight();
            } else {
                offsetY += deltaY;
            }
        }

//        if (!GWT.isScript()) {
//            DOM.setStyleAttribute(inner.getElement(), "border", "green 2px solid");
//        }
//        notifyListeners(new DragScrollEvent(oldX, oldY, offsetX, offsetY, inner));

        moveTo(offsetX, offsetY, 50);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public boolean isAtBottom() {
        return offsetY > outer.getOffsetHeight() - inner.getOffsetHeight() - 40;
    }

    public void scrollToBottom() {
        offsetY = outer.getOffsetHeight() - inner.getOffsetHeight();
        moveTo(offsetX, offsetY, 0);
    }

    @Override
    public void onMouseWheel(@Nonnull final MouseWheelEvent event) {
        moveToDelta(browserUtil.convertMouseScrollDeltaXToPixelDelta(event), browserUtil.convertMouseScrollDeltaYToPixelDelta(event));
        userAction();
    }

    private void mouseScrollHandler(final double deltaX, final double deltaY) {
        moveToDelta(browserUtil.convertMouseScrollDeltaXToPixelDelta(deltaX), browserUtil.convertMouseScrollDeltaYToPixelDelta(deltaY));
        userAction();
    }

    protected void userAction() {
        onUserAction.run();
    }

    //TODO: Temporary hack :-)
    public native void setScrollHandler(VortexScrollPanel app, String objectID) /*-{
        scrollEvent = function (xDelta, yDelta) {
            app.@cazcade.vortex.widgets.client.panels.scroll.VortexScrollPanel::mouseScrollHandler(DD)
                    (Number(xDelta), Number(yDelta));
        };
//        window.alert('id is '+objectID);
        $wnd.setupScrolling(objectID, scrollEvent);
    }-*/;

    public void scrollToX(final double x, final int speed) {
        moveToDelta(offsetX - (int) x, speed);
    }

    public void scrollToY(final double y) {
        moveToDelta(0, offsetY - (int) y);
    }

    public void scrollToTopLeft() {
        moveTo(0, 0, 500);
    }


    private class ScrollAreaPanel extends AbsolutePanel implements HasAllMouseHandlers, HasAllTouchHandlers {

        private void init(final EventBasedGestureController handler) {
            addMouseUpHandler(handler);
            addMouseDownHandler(handler);
            addMouseMoveHandler(handler);
//            addMouseWheelHandler(handler);
            if (browserUtil.isTouchEnabled()) {
                addTouchStartHandler(handler);
                addTouchEndHandler(handler);
                addTouchMoveHandler(handler);
                addTouchCancelHandler(handler);
            }

        }

        public HandlerRegistration addMouseUpHandler(final MouseUpHandler mouseUpHandler) {
            return addDomHandler(mouseUpHandler, MouseUpEvent.getType());
        }

        public HandlerRegistration addMouseOutHandler(final MouseOutHandler mouseOutHandler) {
            return addDomHandler(mouseOutHandler, MouseOutEvent.getType());
        }

        public HandlerRegistration addMouseOverHandler(final MouseOverHandler mouseOverHandler) {
            return addDomHandler(mouseOverHandler, MouseOverEvent.getType());
        }

        public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler mouseWheelHandler) {
            return addDomHandler(mouseWheelHandler, MouseWheelEvent.getType());
        }

        public HandlerRegistration addMouseDownHandler(final MouseDownHandler mouseDownHandler) {
            return addDomHandler(mouseDownHandler, MouseDownEvent.getType());
        }

        public HandlerRegistration addMouseMoveHandler(final MouseMoveHandler mouseMoveHandler) {
            return addDomHandler(mouseMoveHandler, MouseMoveEvent.getType());
        }

        public HandlerRegistration addTouchStartHandler(final TouchStartHandler touchStartHandler) {
            return addDomHandler(touchStartHandler, TouchStartEvent.getType());
        }

        public HandlerRegistration addTouchEndHandler(final TouchEndHandler touchEndHandler) {
            return addDomHandler(touchEndHandler, TouchEndEvent.getType());
        }

        public HandlerRegistration addTouchMoveHandler(final TouchMoveHandler touchMoveHandler) {
            return addDomHandler(touchMoveHandler, TouchMoveEvent.getType());
        }

        public HandlerRegistration addTouchCancelHandler(final TouchCancelHandler touchCancelHandler) {
            return addDomHandler(touchCancelHandler, TouchCancelEvent.getType());
        }

    }
}
