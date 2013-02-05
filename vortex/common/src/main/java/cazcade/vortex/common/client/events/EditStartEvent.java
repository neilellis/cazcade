/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class EditStartEvent extends GwtEvent<EditStartHandler> {
    @Nonnull
    public static final Type<EditStartHandler> TYPE = new Type<EditStartHandler>();

    @Nonnull
    public Type<EditStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final EditStartHandler handler) {
        handler.onEditStart(this);
    }
}
