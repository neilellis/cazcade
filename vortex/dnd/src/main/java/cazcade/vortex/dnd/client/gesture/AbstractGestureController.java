package cazcade.vortex.dnd.client.gesture;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.dnd.client.touch.Touch;
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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractGestureController implements MouseWheelHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler, TouchStartHandler, TouchEndHandler, TouchMoveHandler, TouchCancelHandler {
    public static final int DOUBLE_TAB_THRESHOLD = 300;
    public static final int SHORT_HOLD_THRESHOLD = 200;
    public static final int HOLD_THEN_DRAG_THRESHOLD = 300;
    public static final int LONG_PRESS_THRSHOLD = 1200;
    private static final int FLICK_LOWER_THRESHOLD = 50;
    private static final int FLICK_UPPER_THRESHOLD = 300;
    private static final int DRAG_BOUNDS_TOLERANCE = 40;


    private static final boolean FLICK_SUPPORTED = false;


    final GestureControllable controllable;
    /**
     * Events are passed onto the alternate when the controllable is disabled.
     */
    private final Widget alternate;


    protected int y;
    protected int startY;
    protected int deltaY;
    protected int deltaX;
    protected int startX;
    protected int x;
    protected long startTime;


    protected long duration;
    protected long endTime;
    @Nullable
    protected DomEvent event;
    protected final int offsetX = 0;
    protected final int offsetY = 0;
    protected int oldX;
    protected int oldY;
    @Nullable
    private Timer longPressTimer;
    @Nullable
    private Timer shortPressTimer;
    private long lastTap;
    private boolean moved;
    private boolean withinGesture;
    @Nullable
    private Timer tapTimer;
    private boolean holdThenDragThresholdExceeded;
    private final boolean xMovementAllowed;
    private final boolean yMovementAllowed;
    private final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    private boolean active = true;
    private boolean boundsCheck;


    public AbstractGestureController(final GestureControllable controllable, Widget alternate, boolean xMovementAllowed, boolean yMovementAllowed) {
        this.alternate = alternate;
        this.controllable = controllable;
        this.xMovementAllowed = xMovementAllowed;
        this.yMovementAllowed = yMovementAllowed;
//        Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
//            public void onPreviewNativeEvent(Event.NativePreviewEvent nativePreviewEvent) {
//                if (nativePreviewEvent.getNativeEvent().getType().equals("mouseup")) {
//                    endGesture();
//                }
//            }
//        });
    }

    public void reset() {
        x = 0;
        y = 0;
        startX = 0;
        startY = 0;
        deltaX = 0;
        deltaY = 0;
        startTime = 0;
        duration = 0;
        oldX = 0;
        oldY = 0;
        moved = false;
        withinGesture = false;
        longPressTimer = null;
        shortPressTimer = null;
        tapTimer = null;
        holdThenDragThresholdExceeded = false;
        endTime = 0;
        event = null;
        lastTap = 0;
        controllable.releaseCapture();
    }


    private void endGesture() {
        if (withinGesture) {
            if (longPressTimer != null) {
                longPressTimer.cancel();
                longPressTimer = null;
            }
            withinGesture = false;
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            holdThenDragThresholdExceeded = false;
            if (moved) {
                if (duration < FLICK_UPPER_THRESHOLD && FLICK_SUPPORTED) {
                    ClientLog.log("******* FLICK ******");
                    onFlick();
                } else {
                    ClientLog.log("******* DRAG END ******");
                    onEndDragInternal();
                }
            } else {
                if (duration < SHORT_HOLD_THRESHOLD) {
                    if (startTime - lastTap < DOUBLE_TAB_THRESHOLD) {
                        ClientLog.log("******* DOUBLE TAP ******");
                        tapTimer.cancel();
                        onDoubleTap();
                    } else {
                        lastTap = startTime;
                        tapTimer = new Timer() {
                            @Override
                            public void run() {
                                ClientLog.log("******* SINGLE TAP ******");
                                onTap();
                            }
                        };
                        tapTimer.schedule(DOUBLE_TAB_THRESHOLD);
                    }
                } else if (duration < LONG_PRESS_THRSHOLD) {
                    ClientLog.log("******* SHORT HOLD ******");
                    onShortPress();
                } else {
                    // the long press timer event deals with this
                }
            }
            if (longPressTimer != null) {
                longPressTimer.cancel();
            }
            longPressTimer = null;
            shortPressTimer = null;
            ClientLog.log("******* RELEASE CAPTURE ******");
            controllable.releaseCapture();
        }
    }

    public abstract void onFlick();

    private void onEndDragInternal() {
        oldX = offsetX;
        oldY = offsetY;

        withinGesture = false;
        double multiplier = 1.0;
//        if (deltaTime > FLICK_LOWER_THRESHOLD && deltaTime < FLICK_UPPER_THRESHOLD) {
//            multiplier = (FLICK_UPPER_THRESHOLD - deltaTime) / 10;
//        }
        if (xMovementAllowed) {
            deltaX = (int) ((x - startX) * multiplier);
        }
        if (yMovementAllowed) {
            deltaY = (int) ((y - startY) * multiplier);
        }
//            ClientLog.log("UP", null);


        ClientLog.log("*******DRAG END******", null);
        onEndDrag();
    }

    public abstract void onEndDrag();

    public abstract void onDoubleTap();

    public abstract void onTap();

    public abstract void onShortPress();


    public void onMouseDown(@Nonnull MouseDownEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        this.event = event;
        startX = event.getScreenX();
        startY = event.getScreenY();

        x = startX;
        y = startY;


        startGesture();
        event.stopPropagation();
        event.preventDefault();
    }

    public abstract void onLongPress();

    private void startGesture() {
//        ClientLog.log("CAPTURE ELEMENT " + DOM.getCaptureElement());

        if (DOM.getCaptureElement() != null && !controllable.hasCapture()) {
            DOM.releaseCapture(DOM.getCaptureElement());
        }
        controllable.startCapture();
        startTime = System.currentTimeMillis();
        moved = false;
        longPressTimer = new Timer() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                if (!moved) {
                    ClientLog.log("******* LONG HOLD ******");
//                    DOM.releaseCapture(container.getElement());
                    onLongPress();
                    endGesture();
                }
            }
        };
        longPressTimer.schedule(LONG_PRESS_THRSHOLD);
        shortPressTimer = new Timer() {
            @Override
            public void run() {
                onExceededHoldThenDragThreshold();
                holdThenDragThresholdExceeded = true;
            }
        };
        shortPressTimer.schedule(HOLD_THEN_DRAG_THRESHOLD);
        withinGesture = true;
        ClientLog.log("******* CAPTURE ******");
        onGestureStart();
    }

    public abstract void onExceededHoldThenDragThreshold();

    protected abstract void onGestureStart();


    public final void onMouseMove(@Nonnull MouseMoveEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        if (withinGesture) {
            this.event = event;
            x = event.getScreenX();
            y = event.getScreenY();
            if (boundsCheck) {
                double relativeX = event.getRelativeX(controllable.getBoundingElement());
                double relativeY = event.getRelativeY(controllable.getBoundingElement());
                if (relativeX < controllable.getLeftBounds() - DRAG_BOUNDS_TOLERANCE || relativeX > controllable.getRightBounds() + DRAG_BOUNDS_TOLERANCE
                        || relativeY > controllable.getBottomBounds() + DRAG_BOUNDS_TOLERANCE || relativeY < controllable.getTopBounds() - DRAG_BOUNDS_TOLERANCE) {
                    ClientLog.log("**** GESTURE EXCEEDED BOUNDS ****");
                    ClientLog.log("X= " + relativeX + " left= " + controllable.getLeftBounds());
                    ClientLog.log("X= " + relativeX + " right= " + controllable.getRightBounds());
                    ClientLog.log("Y= " + relativeY + " top= " + controllable.getTopBounds());
                    ClientLog.log("Y= " + relativeY + " bottom= " + controllable.getBottomBounds());
                    endGesture();
                    return;
                }
            }

            event.preventDefault();
            event.stopPropagation();
            onDragInternal();

        }
    }

    private void onDragInternal() {
        ClientLog.log("******* DRAG INTERNAL ******");
        moved = true;
        if (longPressTimer != null) {
            longPressTimer.cancel();
            longPressTimer = null;
        }

//                widget.getElement().setAttribute("style", originalStyle + "; -webkit-transition-property: -webkit-transform; -webkit-transition-duration: 0.1s;-webkit-transform:translate(" + (x + deltaX) + "px," + (y + deltaY) + "px);");
        boolean doMove = false;
        if (xMovementAllowed) {
            deltaX = x - startX;
            if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                doMove = true;
            }
        }
        if (yMovementAllowed) {
            deltaY = y - startY;
            if (Math.abs(deltaY) >= Math.abs(deltaX)) {
                doMove = true;
            }
        }
        if (doMove) {
            if (holdThenDragThresholdExceeded) {
                ClientLog.log("******* HOLD THEN DRAG ******");
                onHoldDrag();
            } else {
                ClientLog.log("******* DRAG ******");
                holdThenDragThresholdExceeded = false;
                if (shortPressTimer != null) {
                    shortPressTimer.cancel();
                    shortPressTimer = null;
                }
                onDrag();
            }
        }
    }

    public abstract void onHoldDrag();


    public abstract void onDrag();


    public final void onMouseUp(@Nonnull MouseUpEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

//        ClientLog.log("******* MOUSE UP ******");
        this.event = event;

//        event.stopPropagation();
        event.preventDefault();
        x = event.getScreenX();
        y = event.getScreenY();
        endGesture();
    }

    public abstract void onMultiTap();

    public abstract void onSend();

    public final void onTouchCancel(@Nonnull final TouchCancelEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        this.event = event;
        longPressTimer.cancel();
        deltaX = 0;
        deltaY = 0;
        event.stopPropagation();
        cancelGesture();
    }

    private void cancelGesture() {
        duration = 0;
        moved = false;
        withinGesture = false;
    }

    public final void onTouchEnd(@Nonnull final TouchEndEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        this.event = event;
        ClientLog.log("*******TOUCH END******");
        if (event.getTargetTouches().length() > 1) {
            endMultiTouchGesture(event);
        } else if (event.getTargetTouches().length() == 1) {
            event.stopPropagation();
            event.preventDefault();
            x = event.getTargetTouches().get(0).getScreenX();
            y = event.getTargetTouches().get(0).getScreenY();
            endGesture();
        } else {
            ClientLog.log("*******NO TOUCHES?******");
            endGesture();
        }
    }

    private void endMultiTouchGesture(TouchEndEvent event) {
        if (!active) {
            return;
        }

        //todo
    }

    public final void onTouchMove(@Nonnull final TouchMoveEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        ClientLog.log("*******TOUCH MOVE******");
        if (withinGesture) {
            this.event = event;

            if (event.getTargetTouches().length() > 1) {
                onMultiTouchMove(event);
            } else if (event.getTargetTouches().length() == 1) {
                event.stopPropagation();
                event.preventDefault();
                final Touch touch = event.getTouches().get(0);
                x = touch.getScreenX();
                y = touch.getScreenY();
                onDragInternal();
            } else {
                ClientLog.log("*******NO TOUCHES?******");
            }
        }
    }

    private void onMultiTouchMove(@Nonnull final TouchMoveEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        ClientLog.log("*******MULTI TOUCH MOVE ******");
        if (event.getTargetTouches().length() == 2) {
            //todo: scale or rotate
        }
    }

    public void onTouchStart(@Nonnull TouchStartEvent event) {
        if (!active) {
            alternate.fireEvent(event);
            return;
        }

        ClientLog.log("*******TOUCH START******");
        this.event = event;
        startGesture();
        startX = 0;
        startY = 0;
        x = 0;
        y = 0;
        deltaX = 0;
        deltaY = 0;
        if (event.getTargetTouches().length() > 1) {
            onMultiTouchStart();
        } else if (event.getTargetTouches().length() == 1) {
            event.preventDefault();
            event.stopPropagation();
            Touch touch = event.getTargetTouches().get(0);
            startY = touch.getScreenY();
            startX = touch.getScreenX();
            x = startX;
            y = startY;

        } else {
            ClientLog.log("*******NO TOUCHES?******");
        }
    }

    public abstract void onMultiTouchStart();

    public void setActive(boolean active) {
        this.active = active;
    }

}