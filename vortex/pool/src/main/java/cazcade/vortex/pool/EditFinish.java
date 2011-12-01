package cazcade.vortex.pool;

import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class EditFinish extends GwtEvent<EditFinishHandler> {
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
