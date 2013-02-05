/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class ValidEvent extends GwtEvent<ValidHandler> {
    public static Type<ValidHandler> TYPE = new Type<ValidHandler>();

    public Type<ValidHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ValidHandler handler) {
        handler.onValid(this);
    }
}
