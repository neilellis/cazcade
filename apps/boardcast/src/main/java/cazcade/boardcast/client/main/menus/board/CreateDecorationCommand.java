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
public class CreateDecorationCommand extends CreateItemCommand {
    private final String urlForDecoration;

    public CreateDecorationCommand(final LiquidURI pool, final LSDDictionaryTypes type, final String urlForDecoration,
                                   final Size size, final String theme) {
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
        final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(getType());
        entity.setAttribute(LSDAttribute.IMAGE_URL, urlForDecoration);
        addDefaultView(entity);
        onBuilt.onBuilt(entity);
    }
}
