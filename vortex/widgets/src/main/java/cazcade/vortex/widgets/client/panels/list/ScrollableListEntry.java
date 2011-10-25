package cazcade.vortex.widgets.client.panels.list;

import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public interface ScrollableListEntry extends IsWidget, Comparable<ScrollableListEntry> {
    LSDEntity getEntity();
    String getListIdentifier();

}
