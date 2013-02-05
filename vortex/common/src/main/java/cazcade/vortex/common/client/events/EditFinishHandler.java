/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author neilellis@cazcade.com
 */
public interface EditFinishHandler extends EventHandler {
    void onEditFinish(EditFinishEvent event);
}
