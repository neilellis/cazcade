package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateCustomObjectCommand extends CreateItemCommand {


    public CreateCustomObjectCommand(final LiquidURI pool, final LSDDictionaryTypes type) {
        super(pool, type);
    }

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(getType());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
