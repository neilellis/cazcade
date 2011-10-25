package cazcade.hashbo.client.main.menus.add;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;

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
