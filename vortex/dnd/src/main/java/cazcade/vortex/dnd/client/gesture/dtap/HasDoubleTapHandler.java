package cazcade.vortex.dnd.client.gesture.dtap;

import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface HasDoubleTapHandler {
    @Nonnull
    HandlerRegistration addTapHandler(DoubleTapHandler handler);
}