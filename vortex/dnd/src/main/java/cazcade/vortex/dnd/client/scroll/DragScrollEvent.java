package cazcade.vortex.dnd.client.scroll;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class DragScrollEvent {

    private final int oldX;
    private final int oldY;
    private final int newX;
    private final int newY;
    private final Widget widget;

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
