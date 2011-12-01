package cazcade.vortex.pool.widgets.dnd;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.dnd.client.gesture.AbstractGestureController;
import cazcade.vortex.dnd.client.touch.cancel.TouchCancelEvent;
import cazcade.vortex.dnd.client.touch.cancel.TouchCancelHandler;
import cazcade.vortex.dnd.client.touch.end.TouchEndEvent;
import cazcade.vortex.dnd.client.touch.end.TouchEndHandler;
import cazcade.vortex.dnd.client.touch.move.TouchMoveEvent;
import cazcade.vortex.dnd.client.touch.move.TouchMoveHandler;
import cazcade.vortex.dnd.client.touch.start.TouchStartEvent;
import cazcade.vortex.dnd.client.touch.start.TouchStartHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public class GestureAwareView extends Composite {

    private final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);

    public GestureAwareView() {
        super();
    }

    public void initHandlers(final AbstractGestureController controller) {
        addMouseUpHandler(controller);
        addMouseDownHandler(controller);
        addMouseMoveHandler(controller);
        if (browserUtil.isTouchEnabled()) {
            addTouchStartHandler(controller);
            addTouchEndHandler(controller);
            addTouchMoveHandler(controller);
            addTouchCancelHandler(controller);
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

    public HandlerRegistration addClickHandler(final ClickHandler clickHandler) {
        return addDomHandler(clickHandler, ClickEvent.getType());
    }

}
