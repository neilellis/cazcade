package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

/**
 * @author neilellis@cazcade.com
 */
public class CreateRichTextCommand extends CreateItemCommand {

    @Override
    protected void buildEntity(BuildCallback onBuilt) {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
//        entity.setAttribute(LSDAttribute.TEXT_EXTENDED, "Double click to edit");
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }

    public CreateRichTextCommand(LiquidURI pool, LSDDictionaryTypes type, Size size, String theme) {
        super(pool, type, size, theme);
    }
}
