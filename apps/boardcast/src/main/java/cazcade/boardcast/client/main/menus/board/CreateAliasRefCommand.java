/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateAliasRefCommand extends CreateItemCommand {
    private final LiquidURI uri;

    public CreateAliasRefCommand(final LiquidURI pool, final Types type, final LiquidURI uri) {
        super(pool, type);
        this.uri = uri;
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final TransferEntity entity = SimpleEntity.create(getType());
        entity.$(Dictionary.SOURCE, uri.asString());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
        Track.getInstance().trackEvent("Add", "Add Business Card");
    }
}
