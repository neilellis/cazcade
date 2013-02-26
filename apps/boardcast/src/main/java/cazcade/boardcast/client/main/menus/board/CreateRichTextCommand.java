/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateRichTextCommand extends CreateItemCommand {
    public CreateRichTextCommand(final LiquidURI pool, final Types type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Text");
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final TransferEntity entity = SimpleEntity.create(getType());
        //        entity.$(Attribute.TEXT_EXTENDED, "Double click to edit");
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
