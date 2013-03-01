/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateChecklistCommand extends CreateContainerCommand {
    public CreateChecklistCommand(final LURI pool, final Types type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Checklist");
    }

    @Nonnull @Override
    protected String getInitialName() {
        return "checklist" + System.currentTimeMillis();
    }
}
