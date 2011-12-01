package cazcade.vortex.dnd.client;

import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class KeepThisDraggableMouseListener
        implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {
    private boolean dragging;

    private final AbsolutePanel container;
    @Nonnull
    private final Widget widget;
    private String originalStyle;
    private int y;
    private int startY;
    private int delta;

    public KeepThisDraggableMouseListener(AbsolutePanel container, @Nonnull Widget widget) {
        this.container = container;
        this.widget = widget;
        originalStyle = widget.getElement().getAttribute("style");
        y = 0;
        delta = 0;

    }


    public void onMouseDown(@Nonnull MouseDownEvent event) {
        dragging = true;

// capturing the mouse to the dragged widget.
        DOM.setCapture(container.getElement());
        startY = event.getY();
        ClientLog.log("DOWN", null);
        event.stopPropagation();
        event.preventDefault();
        originalStyle = widget.getElement().getAttribute("style");
    }


    public void onMouseUp(@Nonnull MouseUpEvent event) {
        dragging = false;
        DOM.releaseCapture(container.getElement());
        ClientLog.log("UP", null);
        event.stopPropagation();
        event.preventDefault();
        DOM.setStyleAttribute(widget.getElement(), "border", "red 2px solid");
        y += delta;

//        widget.getElement().setAttribute("style", originalStyle);
    }


    public void onMouseMove(@Nonnull MouseMoveEvent event) {
        if (dragging) {
// we don't want the widget to go off-screen, so the top/left
// values should always remain be positive.
//            int newX = Math.max(0, event.getRelativeX(widget.getElement()) + widget.getAbsoluteLeft() - dragStartX);
//            int newY = Math.max(0, event.getRelativeY(widget.getElement()) + widget.getAbsoluteTop() - dragStartY);
//            int newX =  event.getRelativeX(widget.getElement()) - dragStartX;
            delta = event.getY() - startY;
//            DOM.setStyleAttribute(widget.getElement(), "left", "" + newX);
//            DOM.setStyleAttribute(widget.getElement(), "top", "" + newY);
//            widget.getElement().setAttribute("style", widget.getElement().getAttribute("style") + ";border: white 2px solid;");

            widget.getElement().setAttribute("style", originalStyle + "; -webkit-transition-property: -webkit-transform; -webkit-transition-duration: 0.1s;-webkit-transform:translate(0px," + (y + delta) + "px);");
            DOM.setStyleAttribute(widget.getElement(), "border", "blue 2px solid");
//            container.setWidgetPosition(widget, newX, newY);
            ClientLog.log("DRAG to " + y, null);
        }
        event.stopPropagation();
        event.preventDefault();
    }


}
