/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class EditFinishEvent extends GwtEvent<EditFinishHandler> {
    @Nonnull
    public static final Type<EditFinishHandler> TYPE = new Type<EditFinishHandler>();

    @Nonnull
    public Type<EditFinishHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final EditFinishHandler handler) {
        handler.onEditFinish(this);
    }
}
