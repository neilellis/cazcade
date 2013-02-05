/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class InvalidEvent extends GwtEvent<InvalidHandler> {
    public static Type<InvalidHandler> TYPE = new Type<InvalidHandler>();

    public Type<InvalidHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(InvalidHandler handler) {
        handler.onInvalid(this);
    }
}
