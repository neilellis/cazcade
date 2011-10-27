package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

/**
 * @author neilellis@cazcade.com
 */
public class CreateDecorationCommand extends CreateItemCommand {


    private String urlForDecoration;

    public CreateDecorationCommand(LiquidURI pool, LSDDictionaryTypes type, String urlForDecoration, Size size, String theme) {
        super(pool, type, size, theme);
        this.urlForDecoration = urlForDecoration;
    }


    @Override
    protected void buildEntity(BuildCallback onBuilt) {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
        entity.setAttribute(LSDAttribute.IMAGE_URL, urlForDecoration);
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
