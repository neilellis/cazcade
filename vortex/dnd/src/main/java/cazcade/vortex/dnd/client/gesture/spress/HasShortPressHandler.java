package cazcade.vortex.dnd.client.gesture.spress;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasShortPressHandler {
    @Nonnull
    HandlerRegistration addShortPressHandler(ShortPressHandler shortPressHandler);
}