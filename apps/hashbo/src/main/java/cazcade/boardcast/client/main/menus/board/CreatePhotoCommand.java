package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.gwt.util.client.analytics.Track;
import cazcade.vortex.pool.objects.photo.PhotoEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreatePhotoCommand extends CreateItemCommand {


    public CreatePhotoCommand(final LiquidURI pool, final LSDDictionaryTypes type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        showEditorPanel(new PhotoEditorPanel(createEntityWithDefaultView()));
        Track.getInstance().trackEvent("Add", "Add Decoration");

    }


    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
