package cazcade.vortex.widgets.client.panels.list;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public interface ScrollableListEntry extends IsWidget, Comparable<ScrollableListEntry> {
    LSDBaseEntity getEntity();

    @Nullable
    String getListIdentifier();

}
