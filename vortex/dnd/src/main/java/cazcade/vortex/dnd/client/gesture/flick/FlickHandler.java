package cazcade.vortex.dnd.client.gesture.flick;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface FlickHandler extends EventHandler {
    void onFlick(FlickEvent flickEvent);
}