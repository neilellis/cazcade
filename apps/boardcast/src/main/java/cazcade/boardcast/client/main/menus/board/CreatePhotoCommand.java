/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.pool.objects.photo.PhotoEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreatePhotoCommand extends CreateItemCommand {
    public CreatePhotoCommand(final LURI pool, final Types type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        showEditorPanel(new PhotoEditorPanel(createEntityWithDefaultView()));
        Track.getInstance().trackEvent("Add", "Add Decoration");
    }

    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
