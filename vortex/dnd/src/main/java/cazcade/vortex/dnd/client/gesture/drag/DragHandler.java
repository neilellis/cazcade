package cazcade.vortex.dnd.client.gesture.drag;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface DragHandler extends EventHandler {
    void onDrag(DragEvent dragEvent);
}