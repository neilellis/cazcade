package cazcade.vortex.dnd.client.touch;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.EventTarget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class Touch extends JavaScriptObject {

    protected Touch() {
    }

    public final native int getPageX() /*-{
        return this.pageX;
    }-*/;

    public final native int getPageY() /*-{
        return this.pageY;
    }-*/;

    public final native int getScreenX() /*-{
        return this.screenX;
    }-*/;

    public final native int getScreenY() /*-{
        return this.screenY;
    }-*/;

    public final native int getClientX() /*-{
        return this.clientX;
    }-*/;

    public final native int getClientY() /*-{
        return this.clientY;
    }-*/;

    @Nonnull
    public final native EventTarget getTarget() /*-{
        return this.eventTarget;
    }-*/;


    public final native int getId() /*-{
        return this.identifier;
    }-*/;
}
