package cazcade.vortex.dnd.client.gesture.flick;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author neilellis@cazcade.com
 */
public interface HasFlickHandler {
     HandlerRegistration addFlickHandler(FlickHandler flickHandler);
}