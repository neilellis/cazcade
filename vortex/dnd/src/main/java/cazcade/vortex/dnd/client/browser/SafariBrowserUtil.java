package cazcade.vortex.dnd.client.browser;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class SafariBrowserUtil extends BrowserUtil {

    @Override
    public void initDraggable(@Nonnull Widget widget) {
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransitionProperty", "-webkit-transform, left, top");
        DOM.setStyleAttribute(widget.getElement(), "webkitTransformStyle", "preserve-3d");
    }

    @Override
    public void translateXY(@Nonnull Widget widget, double x, double y, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "webkitTransform", "translate3d(" + x + "px," + y + "px, 0px)");
    }

    @Override
    public boolean isTouchEnabled() {
        return false;
    }

    @Override
    public void resize(@Nonnull Widget widget, Integer width, Integer height, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "width", width + "px");
        DOM.setStyleAttribute(widget.getElement(), "height", height + "px");
    }

    @Override
    public void rotate(@Nonnull Widget widget, Double rotationInDegrees, int millisecondTransitionTime) {
//        DOM.setStyleAttribute(widget.getElement(), "webkitTransitionDuration", millisecondTransitionTime + "ms");
        DOM.setStyleAttribute(widget.getElement(), "webkitTransform", "rotate(" + rotationInDegrees + "deg)");
    }

    @Override
    public boolean isVisibleKeyPress(int keyCode) {
        return (keyCode != KeyCodes.KEY_TAB)
                && (keyCode != KeyCodes.KEY_BACKSPACE)
                && (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER)
                && (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
                && (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
                && (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN);
    }
}