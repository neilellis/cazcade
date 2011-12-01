package cazcade.vortex.dnd.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class FirefoxBrowserUtil extends BrowserUtil {


    @Override
    public void initDraggable(final Widget widget) {
//        DOM.setStyleAttribute(widget.getElement(), "mozTransitionProperty", "-moz-transform, top, left");
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransformStyle", "preserve-3d");
    }

    @Override
    public void translateXY(@Nonnull final Widget widget, final double x, final double y, final int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "MozTransform", "translate(" + x + "px," + y + "px)");
    }

    @Override
    public boolean isTouchEnabled() {
        return false;
    }

    @Override
    public void resize(@Nonnull final Widget widget, final Integer width, final Integer height, final int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "width", width + "px");
        DOM.setStyleAttribute(widget.getElement(), "height", height + "px");
    }

    @Override
    public void rotate(@Nonnull final Widget widget, final Double rotationInDegrees, final int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "transitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "MozTransform", "rotate(" + rotationInDegrees + "deg)");
    }


}
