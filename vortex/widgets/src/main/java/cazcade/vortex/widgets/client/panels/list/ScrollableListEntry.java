/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.panels.list;

import cazcade.liquid.api.lsd.Entity;
import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public interface ScrollableListEntry extends IsWidget, Comparable<ScrollableListEntry> {
    Entity getEntity();

    @Nullable String getListIdentifier();

}
