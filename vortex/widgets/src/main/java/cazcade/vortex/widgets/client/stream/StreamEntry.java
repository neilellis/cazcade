package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public interface StreamEntry extends IsWidget {
    LSDBaseEntity getEntity();

    @Nullable
    String getStreamIdentifier();

    @Nullable
    Date getSortDate();

    int getAutoDeleteLifetime();
}
