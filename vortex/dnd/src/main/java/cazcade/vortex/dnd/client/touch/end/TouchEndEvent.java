package cazcade.vortex.dnd.client.touch.end;

import cazcade.vortex.dnd.client.touch.TouchEvent;

/**
 * @author neilellis@cazcade.com
 */
public class TouchEndEvent extends TouchEvent<TouchEndHandler> {

    private static final Type<TouchEndHandler> TYPE =new Type<TouchEndHandler>("touchend", new TouchEndEvent());

    public static Type<TouchEndHandler> getType() {
         return TYPE;
    }


    public final Type<TouchEndHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TouchEndHandler handler) {
        handler.onTouchEnd(this);
    }
}