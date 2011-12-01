package cazcade.vortex.dnd.client.touch.end;

import cazcade.vortex.dnd.client.touch.TouchEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class TouchEndEvent extends TouchEvent<TouchEndHandler> {

    @Nonnull
    private static final Type<TouchEndHandler> TYPE = new Type<TouchEndHandler>("touchend", new TouchEndEvent());

    @Nonnull
    public static Type<TouchEndHandler> getType() {
        return TYPE;
    }


    @Nonnull
    public final Type<TouchEndHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final TouchEndHandler handler) {
        handler.onTouchEnd(this);
    }
}