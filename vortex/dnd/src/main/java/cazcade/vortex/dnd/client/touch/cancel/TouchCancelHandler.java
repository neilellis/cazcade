package cazcade.vortex.dnd.client.touch.cancel;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface TouchCancelHandler extends EventHandler {

    void onTouchCancel(TouchCancelEvent touchCancelEvent);
}