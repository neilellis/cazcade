package cazcade.vortex.dnd.client.touch.move;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTouchMoveHandler {
    HandlerRegistration addTouchMoveHandler(TouchMoveHandler touchMoveHandler);
}