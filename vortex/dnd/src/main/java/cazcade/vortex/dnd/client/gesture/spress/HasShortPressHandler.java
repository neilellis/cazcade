package cazcade.vortex.dnd.client.gesture.spress;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasShortPressHandler {
     HandlerRegistration addShortPressHandler(ShortPressHandler shortPressHandler);
}