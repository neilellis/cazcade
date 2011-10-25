package cazcade.vortex.dnd.client.touch.move;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface TouchMoveHandler extends EventHandler {

    void onTouchMove(TouchMoveEvent touchMoveEvent);
}