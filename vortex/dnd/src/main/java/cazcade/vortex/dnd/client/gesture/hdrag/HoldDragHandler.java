package cazcade.vortex.dnd.client.gesture.hdrag;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface HoldDragHandler extends EventHandler {
    void onHoldDrag(HoldDragEvent holdDragEvent);
}