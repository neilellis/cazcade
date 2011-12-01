package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateRichTextCommand extends CreateItemCommand {

    @Override
    protected void buildEntity(@Nonnull final BuildCallback onBuilt) {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
//        entity.setAttribute(LSDAttribute.TEXT_EXTENDED, "Double click to edit");
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }

    public CreateRichTextCommand(final LiquidURI pool, final LSDDictionaryTypes type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Text");

    }
}
