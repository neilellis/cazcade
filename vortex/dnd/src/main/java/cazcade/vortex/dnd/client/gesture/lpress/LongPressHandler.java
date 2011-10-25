package cazcade.vortex.dnd.client.gesture.lpress;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface LongPressHandler extends EventHandler {
    void onLongPress(LongPressEvent longPressEvent);
}