package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

/**
 * @author neilellis@cazcade.com
 */
public class CreateCustomObjectCommand extends CreateItemCommand {


    public CreateCustomObjectCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }

    @Override
    protected void buildEntity(final BuildCallback onBuilt) {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
