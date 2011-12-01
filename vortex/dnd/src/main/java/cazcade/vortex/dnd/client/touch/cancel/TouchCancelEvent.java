package cazcade.vortex.dnd.client.touch.cancel;

import cazcade.vortex.dnd.client.touch.TouchEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class TouchCancelEvent extends TouchEvent<TouchCancelHandler> {

    @Nonnull
    private static final Type<TouchCancelHandler> TYPE = new Type<TouchCancelHandler>("touchcancel", new TouchCancelEvent());

    @Nonnull
    public static Type<TouchCancelHandler> getType() {
        return TYPE;
    }


    @Nonnull
    public final Type<TouchCancelHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull TouchCancelHandler handler) {
        handler.onTouchCancel(this);
    }
}