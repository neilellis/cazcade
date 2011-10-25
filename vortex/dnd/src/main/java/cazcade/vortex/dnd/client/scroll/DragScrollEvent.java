package cazcade.vortex.dnd.client.scroll;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class DragScrollEvent {

    private int oldX;
    private int oldY;
    private int newX;
    private int newY;
    private Widget widget;

    public DragScrollEvent(int oldX, int oldY, int newX, int newY, Widget widget) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.widget = widget;
    }

    public int getOldX() {
        return oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }

    public Widget getWidget() {
        return widget;
    }
}
