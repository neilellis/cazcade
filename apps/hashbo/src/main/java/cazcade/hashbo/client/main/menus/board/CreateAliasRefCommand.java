package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

/**
 * @author neilellis@cazcade.com
 */
public class CreateAliasRefCommand extends CreateItemCommand {


    private LiquidURI uri;

    public CreateAliasRefCommand(LiquidURI pool, LSDDictionaryTypes type, LiquidURI uri) {
        super(pool, type);
        this.uri = uri;
    }

    @Override
    protected void buildEntity(final BuildCallback onBuilt) {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
        entity.setAttribute(LSDAttribute.SOURCE, uri.asString());
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
