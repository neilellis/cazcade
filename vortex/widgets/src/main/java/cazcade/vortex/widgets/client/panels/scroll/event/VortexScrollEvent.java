package cazcade.vortex.widgets.client.panels.scroll.event;

import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class VortexScrollEvent extends GwtEvent<VortexScrollHandler> {
    @Nonnull
    public static final Type<VortexScrollHandler> TYPE = new Type<VortexScrollHandler>();

    @Nonnull
    public Type<VortexScrollHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull VortexScrollHandler handler) {
        handler.onVortexScroll(this);
    }
}
