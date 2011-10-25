package cazcade.vortex.dnd.client.gesture.tap;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface TapHandler extends EventHandler {
    void onTap(TapEvent tapEvent);
}
