package cazcade.vortex.dnd.client.gesture.tap;

import cazcade.vortex.dnd.client.touch.start.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTapHandler {
     HandlerRegistration addTapHandler(TapHandler touchStartHandler);
}