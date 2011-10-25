package cazcade.vortex.dnd.client.touch;

import cazcade.vortex.dnd.client.touch.cancel.HasTouchCancelHandler;
import cazcade.vortex.dnd.client.touch.end.HasTouchEndHandler;
import cazcade.vortex.dnd.client.touch.move.HasTouchMoveHandler;
import cazcade.vortex.dnd.client.touch.start.HasTouchStartHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface HasAllTouchHandlers extends HasTouchStartHandler, HasTouchMoveHandler, HasTouchCancelHandler, HasTouchEndHandler {
}
