package cazcade.vortex.dnd.client.touch.start;

import cazcade.vortex.dnd.client.touch.TouchEvent;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * @author neilellis@cazcade.com
 */
public class TouchStartEvent  extends TouchEvent<TouchStartHandler> {

    private static final DomEvent.Type<TouchStartHandler> TYPE =new DomEvent.Type<TouchStartHandler>("touchstart", new TouchStartEvent());

    public static DomEvent.Type<TouchStartHandler> getType() {
         return TYPE;
    }


    public final DomEvent.Type<TouchStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TouchStartHandler handler) {
        handler.onTouchStart(this);
    }
}