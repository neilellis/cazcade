package cazcade.vortex.dnd.client.touch;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public abstract class TouchEvent<H extends EventHandler> extends DomEvent<H> {

    public JsArray<Touch> getTouches() {
        return getTouches(getNativeEvent());
    }

    public JsArray<Touch> getTargetTouches() {
        return getTargetTouches(getNativeEvent());
    }

    private native JsArray<Touch> getTouches(NativeEvent nativeEvent) /*-{
      return nativeEvent.touches;
    }-*/;
    private native JsArray<Touch> getTargetTouches(NativeEvent nativeEvent) /*-{
      return nativeEvent.targetTouches;
    }-*/;


}