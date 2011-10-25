package cazcade.vortex.dnd.client.touch.cancel;

import cazcade.vortex.dnd.client.touch.TouchEvent;

/**
 * @author neilellis@cazcade.com
 */
public class TouchCancelEvent extends TouchEvent<TouchCancelHandler> {

    private static final Type<TouchCancelHandler> TYPE =new Type<TouchCancelHandler>("touchcancel", new TouchCancelEvent());

    public static Type<TouchCancelHandler> getType() {
         return TYPE;
    }


    public final Type<TouchCancelHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TouchCancelHandler handler) {
        handler.onTouchCancel(this);
    }
}