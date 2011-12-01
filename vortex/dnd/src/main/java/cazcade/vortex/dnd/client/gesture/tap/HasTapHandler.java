package cazcade.vortex.dnd.client.gesture.tap;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasTapHandler {
    @Nonnull
    HandlerRegistration addTapHandler(TapHandler touchStartHandler);
}