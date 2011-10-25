package cazcade.vortex.dnd.client.gesture.start;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface GestureStartHandler extends EventHandler {
    void onGestureStart(GestureStartEvent gestureStartEvent);
}