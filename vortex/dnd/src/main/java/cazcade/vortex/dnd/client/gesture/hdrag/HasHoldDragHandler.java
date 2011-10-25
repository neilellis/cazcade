package cazcade.vortex.dnd.client.gesture.hdrag;

import cazcade.vortex.dnd.client.gesture.drag.DragHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasHoldDragHandler {
     HandlerRegistration addHoldDragHandler(HoldDragHandler dragHandler);
}