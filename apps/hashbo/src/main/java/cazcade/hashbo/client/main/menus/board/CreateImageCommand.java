package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

/**
 * @author neilellis@cazcade.com
 */
public class CreateImageCommand extends CreateItemCommand {

    @Override
    protected void buildEntity(BuildCallback onBuilt) {
        //TODO
    }

    public CreateImageCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }
}
