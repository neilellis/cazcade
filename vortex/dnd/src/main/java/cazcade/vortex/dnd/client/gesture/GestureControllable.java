package cazcade.vortex.dnd.client.gesture;

import cazcade.vortex.dnd.client.gesture.start.GestureStartEvent;
import cazcade.vortex.dnd.client.gesture.start.GestureStartHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public interface GestureControllable {
   

    boolean hasCapture();

    void startCapture();

    void releaseCapture();

    void fireEvent(GwtEvent<?> event);


    double getLeftBounds();

    double getRightBounds();

    double getBottomBounds();

    double getTopBounds();

    Element getBoundingElement();
}
