package cazcade.vortex.dnd.client.gesture.spress;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface ShortPressHandler extends EventHandler {
    void onShortPress(ShortPressEvent shortPressEvent);
}