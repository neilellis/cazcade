package cazcade.vortex.dnd.client.gesture;

import cazcade.vortex.dnd.client.gesture.drag.DragEvent;
import cazcade.vortex.dnd.client.gesture.dtap.DoubleTapEvent;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragEvent;
import cazcade.vortex.dnd.client.gesture.flick.FlickEvent;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragEvent;
import cazcade.vortex.dnd.client.gesture.lpress.LongPressEvent;
import cazcade.vortex.dnd.client.gesture.spress.ShortPressEvent;
import cazcade.vortex.dnd.client.gesture.start.GestureStartEvent;
import cazcade.vortex.dnd.client.gesture.tap.TapEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class EventBasedGestureController extends AbstractGestureController {

    public EventBasedGestureController(GestureControllable widget, Widget alternate,boolean xMovementAllowed, boolean yMovementAllowed) {
        super(widget, alternate, xMovementAllowed, yMovementAllowed);
    }

    @Override
    protected void onGestureStart() {
        controllable.fireEvent(new GestureStartEvent(event, startTime, x, y));
    }

    @Override
    public void onExceededHoldThenDragThreshold() {
        //TODO
    }

    @Override
    public void onLongPress() {
        controllable.fireEvent(new LongPressEvent(duration, endTime, event, startTime, x, y));
    }

    @Override
    public void onHoldDrag() {
        controllable.fireEvent(new HoldDragEvent(deltaX, deltaY, event, startTime, startX, startY, x, y, offsetX, offsetY));
    }

    @Override
    public void onDrag() {
        controllable.fireEvent(new DragEvent(deltaX, deltaY, event, startTime, startX, startY, x, y, offsetX, offsetY));
    }

    @Override
    public void onFlick() {
        controllable.fireEvent(new FlickEvent(deltaX, deltaY, duration, endTime, event, startTime, startX, startY, x, y, offsetX, offsetY));
    }

    @Override
    public void onEndDrag() {
        controllable.fireEvent(new EndDragEvent(deltaX, deltaY, duration, endTime, event, startTime, startX, startY, x, y, offsetX, offsetY));
    }

    @Override
    public void onDoubleTap() {
        controllable.fireEvent(new DoubleTapEvent(duration, endTime, event, startTime, x, y));
    }

    @Override
    public void onTap() {
        controllable.fireEvent(new TapEvent(duration, endTime, event, startTime, x, y));
    }

    @Override
    public void onShortPress() {
        controllable.fireEvent(new ShortPressEvent(duration, endTime, event, startTime, x, y));
    }

    @Override
    public void onMultiTap() {
//        widget.fireEvent(new MultiTapEvent(duration,endTime, event, startTime, x, y));
    }

    @Override
    public void onSend() {
        //TODO
    }

    @Override
    public void onMultiTouchStart() {
        //TODO
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        controllable.fireEvent(event);
    }

    @Override
    public void onClick(ClickEvent event) {
        controllable.fireEvent(event);
    }
}
