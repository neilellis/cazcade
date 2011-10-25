package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public interface StreamEntry extends IsWidget {
    LSDEntity getEntity();
    String getStreamIdentifier();
    Date getSortDate();

    int getAutoDeleteLifetime();
}
