package cazcade.vortex.dnd.client.gesture.enddrag;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface EndDragHandler extends EventHandler {
    void onEndDrag(EndDragEvent endDragEvent);
}