package cazcade.vortex.pool;

import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class EditStart extends GwtEvent<EditStartHandler> {
    @Nonnull
    public static final Type<EditStartHandler> TYPE = new Type<EditStartHandler>();

    @Nonnull
    public Type<EditStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull EditStartHandler handler) {
        handler.onEditStart(this);
    }
}
