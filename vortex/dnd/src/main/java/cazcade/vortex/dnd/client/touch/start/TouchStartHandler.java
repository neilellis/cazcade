package cazcade.vortex.dnd.client.touch.start;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface TouchStartHandler  extends EventHandler {

    void onTouchStart(TouchStartEvent touchStartEvent);
}