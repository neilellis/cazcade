package cazcade.vortex.dnd.client.touch.move;

import cazcade.vortex.dnd.client.touch.TouchEvent;

/**
 * @author neilellis@cazcade.com
 */
public class TouchMoveEvent extends TouchEvent<TouchMoveHandler> {

    private static final Type<TouchMoveHandler> TYPE =new Type<TouchMoveHandler>("touchmove", new TouchMoveEvent());

    public static Type<TouchMoveHandler> getType() {
         return TYPE;
    }


    public final Type<TouchMoveHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TouchMoveHandler handler) {
        handler.onTouchMove(this);
    }
}