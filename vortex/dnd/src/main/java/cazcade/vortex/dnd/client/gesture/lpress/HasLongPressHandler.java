package cazcade.vortex.dnd.client.gesture.lpress;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasLongPressHandler {
     HandlerRegistration addLongPressHandler(LongPressHandler longPressHandler);
}