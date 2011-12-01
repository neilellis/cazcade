package cazcade.vortex.dnd.client.touch.start;

import cazcade.vortex.dnd.client.touch.TouchEvent;
import com.google.gwt.event.dom.client.DomEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class TouchStartEvent extends TouchEvent<TouchStartHandler> {

    @Nonnull
    private static final DomEvent.Type<TouchStartHandler> TYPE = new DomEvent.Type<TouchStartHandler>("touchstart", new TouchStartEvent());

    @Nonnull
    public static DomEvent.Type<TouchStartHandler> getType() {
        return TYPE;
    }


    @Nonnull
    public final DomEvent.Type<TouchStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final TouchStartHandler handler) {
        handler.onTouchStart(this);
    }
}