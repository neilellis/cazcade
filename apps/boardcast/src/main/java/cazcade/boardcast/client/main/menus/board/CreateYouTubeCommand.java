/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.pool.objects.youtube.YouTubeEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreateYouTubeCommand extends CreateItemCommand {
    public CreateYouTubeCommand(final LiquidURI pool, final Types type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        showEditorPanel(new YouTubeEditorPanel(createEntityWithDefaultView()));
        Track.getInstance().trackEvent("Add", "Add YouTube");
    }

    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
