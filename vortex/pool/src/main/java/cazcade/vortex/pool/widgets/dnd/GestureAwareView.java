package cazcade.vortex.pool.widgets.dnd;

import cazcade.vortex.dnd.client.gesture.AbstractGestureController;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
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
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author neilellis@cazcade.com
 */
public class GestureAwareView extends Composite {

    private BrowserUtil browserUtil = GWT.create(BrowserUtil.class);

    public GestureAwareView() {
    }

    public void initHandlers(AbstractGestureController controller) {
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

    public HandlerRegistration addMouseOutHandler(MouseOutHandler mouseOutHandler) {
        return addDomHandler(mouseOutHandler, MouseOutEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler mouseOverHandler) {
        return addDomHandler(mouseOverHandler, MouseOverEvent.getType());
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler mouseWheelHandler) {
        return addDomHandler(mouseWheelHandler, MouseWheelEvent.getType());
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler mouseDownHandler) {
        return addDomHandler(mouseDownHandler, MouseDownEvent.getType());
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler mouseMoveHandler) {
        return addDomHandler(mouseMoveHandler, MouseMoveEvent.getType());
    }

    public HandlerRegistration addTouchStartHandler(TouchStartHandler touchStartHandler) {
        return addDomHandler(touchStartHandler, TouchStartEvent.getType());
    }

    public HandlerRegistration addTouchEndHandler(TouchEndHandler touchEndHandler) {
        return addDomHandler(touchEndHandler, TouchEndEvent.getType());
    }

    public HandlerRegistration addTouchMoveHandler(TouchMoveHandler touchMoveHandler) {
        return addDomHandler(touchMoveHandler, TouchMoveEvent.getType());
    }

    public HandlerRegistration addTouchCancelHandler(TouchCancelHandler touchCancelHandler) {
        return addDomHandler(touchCancelHandler, TouchCancelEvent.getType());
    }

    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return addDomHandler(clickHandler, ClickEvent.getType());
    }

}
