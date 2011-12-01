package cazcade.vortex.dnd.client.gesture.lpress;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasLongPressHandler {
    @Nonnull
    HandlerRegistration addLongPressHandler(LongPressHandler longPressHandler);
}