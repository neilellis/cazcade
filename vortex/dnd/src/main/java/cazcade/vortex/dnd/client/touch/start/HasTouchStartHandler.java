package cazcade.vortex.dnd.client.touch.start;

import cazcade.vortex.dnd.client.touch.start.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTouchStartHandler {
     HandlerRegistration addTouchStartHandler(TouchStartHandler touchStartHandler);
}
