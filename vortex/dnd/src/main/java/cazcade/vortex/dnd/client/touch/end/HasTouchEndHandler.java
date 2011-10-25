package cazcade.vortex.dnd.client.touch.end;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTouchEndHandler {
    HandlerRegistration addTouchEndHandler(TouchEndHandler touchEndHandler);
}