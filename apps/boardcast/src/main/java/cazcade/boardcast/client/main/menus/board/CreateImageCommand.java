/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;

/**
 * @author neilellis@cazcade.com
 */
public class CreateImageCommand extends CreateItemCommand {
    public CreateImageCommand(final LURI pool, final Types type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Image");
    }

    @Override
    protected void buildEntity(final BuildCallback onBuilt) {
        //TODO
    }
}
