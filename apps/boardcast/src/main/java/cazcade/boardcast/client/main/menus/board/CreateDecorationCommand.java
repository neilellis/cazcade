/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateDecorationCommand extends CreateItemCommand {
    private final String urlForDecoration;

    public CreateDecorationCommand(final LURI pool, final Types type, final String urlForDecoration, final Size size, final String theme) {
        super(pool, type, size, theme);
        this.urlForDecoration = urlForDecoration;
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Decoration");
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final TransferEntity entity = SimpleEntity.create(getType());
        entity.$(Dictionary.IMAGE_URL, urlForDecoration);
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
