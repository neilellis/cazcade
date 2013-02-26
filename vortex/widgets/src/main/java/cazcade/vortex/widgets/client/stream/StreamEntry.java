/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.Entity;
import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public interface StreamEntry extends IsWidget {
    Entity getEntity();

    @Nullable String getStreamIdentifier();

    @Nullable Date getSortDate();

    int getAutoDeleteLifetime();
}
