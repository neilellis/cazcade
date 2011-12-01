package cazcade.vortex.dnd.client.gesture.start;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasGestureStartHandler {
    @Nonnull
    HandlerRegistration addGestureStartHandler(GestureStartHandler handler);
}