package cazcade.vortex.dnd.client.browser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class BrowserUtil {
    public static String convertRelativeUrlToAbsolute(String url) {
        if (url.startsWith("./")) {
            url = url.substring(2);
        }
        String cleanUrl = Window.Location.getHref().split("\\?")[0].split("#")[0];
        return URL.encode(cleanUrl.substring(0, cleanUrl.lastIndexOf("/")) + "/" + url);
    }


    //  Refer to the following URL when implementing subclasses
    //    //http://www.zachstronaut.com/posts/2009/02/17/animate-css-transforms-firefox-webkit.html


    public void initDraggable(Widget draggable) {
        throw new UnsupportedOperationException("This browser is not supported.");
    }

    public void translateXY(Widget draggable, double x, double y, int millisecondTransitionTime) {
        throw new UnsupportedOperationException("This browser is not supported.");
    }

    public boolean isTouchEnabled() {
        return false;
    }

    public void resize(Widget widget, Integer width, Integer height, int millisecondTransitionTime) {
        throw new UnsupportedOperationException("This browser is not supported.");
    }

    public void rotate(Widget widget, Double rotationInDegrees, int millisecondTransitionTime) {
        throw new UnsupportedOperationException("This browser is not supported.");
    }

    public boolean isVisibleKeyPress(int keyCode) {
        return keyCode > 46;
    }

    public int convertMouseScrollDeltaYToPixelDelta(MouseWheelEvent event) {
        return -this.getDeltaY(event.getNativeEvent()) * 10;
    }

    public int convertMouseScrollDeltaXToPixelDelta(MouseWheelEvent event) {
        return -this.getDeltaX(event.getNativeEvent()) * 10;
    }

    /*
     * This is how Webkit rocks, for other browsers check out:
     *
     * http://closure-library.googlecode.com/svn/docs/closure_goog_events_mousewheelhandler.js.source.html
     */

    private final native int getDeltaX(NativeEvent evt)/*-{
        return evt.deltaX;
    }-*/;

    private final native int getDeltaY(NativeEvent evt)/*-{
        return evt.deltaY;
    }-*/;

    public double convertMouseScrollDeltaXToPixelDelta(double deltaX) {
        return deltaX * 40;
    }

    public double convertMouseScrollDeltaYToPixelDelta(double deltaY) {
        return deltaY * 40;
    }

    public int getScreenTop(UIObject w) {
        return getScreenTop(w.getElement());
    }

    public int getScreenLeft(UIObject w) {
        return getScreenLeft(w.getElement());
    }

    private final native int getScreenTop(Element el)/*-{
        var _y = 0;
        while (el && !isNaN(el.offsetTop)) {
            _y += el.offsetTop - el.scrollTop;
            el = el.offsetParent;
        }
        return _y;

    }-*/;

    private final native int getScreenLeft(Element el)/*-{
        var _x = 0;
        while (el && !isNaN(el.offsetLeft)) {
            _x += el.offsetLeft - el.scrollLeft;
            el = el.offsetParent;
        }
        return  _x;
    }-*/;

    public static boolean isInternalImage(String url) {
        int pathStart = url.indexOf("/", url.indexOf(':') + 3);
        final String pathString = url.substring(pathStart + 1);
        return (pathString.startsWith("_images") || pathString.startsWith("_decorations") || pathString.startsWith("_background"));
    }

    public static boolean isImage(String url) {
        if (url == null) {
            return false;
        }
        final String urlLowerCase = url.toLowerCase();
        return (urlLowerCase.endsWith(".jpg") || urlLowerCase.endsWith(".jpeg") || urlLowerCase.endsWith(".gif") || urlLowerCase.endsWith(".png") || urlLowerCase.endsWith(".tiff"));
    }
}
