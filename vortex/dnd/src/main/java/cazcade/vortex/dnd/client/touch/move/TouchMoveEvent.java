package cazcade.vortex.dnd.client.touch.move;

import cazcade.vortex.dnd.client.touch.TouchEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class TouchMoveEvent extends TouchEvent<TouchMoveHandler> {

    @Nonnull
    private static final Type<TouchMoveHandler> TYPE = new Type<TouchMoveHandler>("touchmove", new TouchMoveEvent());

    @Nonnull
    public static Type<TouchMoveHandler> getType() {
        return TYPE;
    }


    @Nonnull
    public final Type<TouchMoveHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull TouchMoveHandler handler) {
        handler.onTouchMove(this);
    }
}