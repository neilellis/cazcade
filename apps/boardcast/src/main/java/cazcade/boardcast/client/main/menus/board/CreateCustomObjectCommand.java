/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateCustomObjectCommand extends CreateItemCommand {
    public CreateCustomObjectCommand(final LURI pool, final Types type) {
        super(pool, type);
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final TransferEntity entity = SimpleEntity.create(getType());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
