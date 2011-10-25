package cazcade.vortex.dnd.client.gesture.dtap;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface DoubleTapHandler extends EventHandler {
    void onDoubleTap(DoubleTapEvent doubleTapEvent);
}