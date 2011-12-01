package cazcade.vortex.dnd.client.gesture.flick;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasFlickHandler {
    @Nonnull
    HandlerRegistration addFlickHandler(FlickHandler flickHandler);
}