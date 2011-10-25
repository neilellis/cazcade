package cazcade.vortex.dnd.client.gesture.enddrag;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasEndDragHandler {
     HandlerRegistration addEndDragHandler(EndDragHandler endDragHandler);
}