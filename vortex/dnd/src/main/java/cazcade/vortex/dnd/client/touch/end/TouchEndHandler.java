package cazcade.vortex.dnd.client.touch.end;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface TouchEndHandler extends EventHandler {

    void onTouchEnd(TouchEndEvent touchEndEvent);
}