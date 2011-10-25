package cazcade.vortex.dnd.client;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public abstract class GestureEvent<H extends EventHandler> extends GwtEvent<H> {

    private int deltaX;
    private int deltaY;
    private long duration;
    private long endTime;
    private DomEvent mostRecentDOMEvent;
    private long startTime;
    private int startX;
    private int startY;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;

    protected GestureEvent(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    protected GestureEvent(int deltaX, int deltaY, long duration, long endTime, DomEvent mostRecentDOMEvent, long startTime, int startX, int startY, int x, int y, int offsetX, int offsetY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.duration = duration;
        this.endTime = endTime;
        this.mostRecentDOMEvent = mostRecentDOMEvent;
        this.startTime = startTime;
        this.startX = startX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public long getDuration() {
        return duration;
    }

    public long getEndTime() {
        return endTime;
    }

    public DomEvent getMostRecentDOMEvent() {
        return mostRecentDOMEvent;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * @deprecated
     * @return
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * @deprecated
     * @return
     */
    public int getOffsetY() {
        return offsetY;
    }
}
