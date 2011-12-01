package cazcade.vortex.dnd.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class FirefoxBrowserUtil extends BrowserUtil {


    @Override
    public void initDraggable(Widget widget) {
//        DOM.setStyleAttribute(widget.getElement(), "mozTransitionProperty", "-moz-transform, top, left");
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransformStyle", "preserve-3d");
    }

    @Override
    public void translateXY(@Nonnull Widget widget, double x, double y, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "MozTransform", "translate(" + x + "px," + y + "px)");
    }

    @Override
    public boolean isTouchEnabled() {
        return false;
    }

    @Override
    public void resize(@Nonnull Widget widget, Integer width, Integer height, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "width", width + "px");
        DOM.setStyleAttribute(widget.getElement(), "height", height + "px");
    }

    @Override
    public void rotate(@Nonnull Widget widget, Double rotationInDegrees, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "MozTransform", "rotate(" + rotationInDegrees + "deg)");
    }


}
