package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateAliasRefCommand extends CreateItemCommand {


    private final LiquidURI uri;

    public CreateAliasRefCommand(final LiquidURI pool, final LSDDictionaryTypes type, final LiquidURI uri) {
        super(pool, type);
        this.uri = uri;
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(getType());
        entity.setAttribute(LSDAttribute.SOURCE, uri.asString());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
        Track.getInstance().trackEvent("Add", "Add Business Card");
    }
}
