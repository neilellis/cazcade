/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class EditCancelEvent extends GwtEvent<EditCancelHandler> {
    public static Type<EditCancelHandler> TYPE = new Type<EditCancelHandler>();

    public Type<EditCancelHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditCancelHandler handler) {
        handler.onEditCancel(this);
    }
}
