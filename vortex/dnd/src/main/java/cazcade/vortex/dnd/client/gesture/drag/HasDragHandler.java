package cazcade.vortex.dnd.client.gesture.drag;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasDragHandler {
     HandlerRegistration addDragHandler(DragHandler dragHandler);
}