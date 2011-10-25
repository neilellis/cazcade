package cazcade.vortex.dnd.client.gesture.dtap;

import cazcade.vortex.dnd.client.touch.start.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasDoubleTapHandler {
     HandlerRegistration addTapHandler(DoubleTapHandler handler);
}