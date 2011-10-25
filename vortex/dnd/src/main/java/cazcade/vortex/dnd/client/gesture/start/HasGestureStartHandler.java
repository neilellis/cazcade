package cazcade.vortex.dnd.client.gesture.start;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasGestureStartHandler {
     HandlerRegistration addGestureStartHandler(GestureStartHandler handler);
}