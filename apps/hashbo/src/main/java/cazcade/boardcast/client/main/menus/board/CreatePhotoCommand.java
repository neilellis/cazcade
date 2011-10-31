package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.pool.objects.photo.PhotoEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreatePhotoCommand extends CreateItemCommand {


    public CreatePhotoCommand(LiquidURI pool, LSDDictionaryTypes type, Size size, String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        showEditorPanel(new PhotoEditorPanel(createEntityWithDefaultView()));
    }


    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
