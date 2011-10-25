package cazcade.vortex.dnd.client.touch.cancel;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTouchCancelHandler {
    HandlerRegistration addTouchCancelHandler(TouchCancelHandler touchCancelHandler);
}