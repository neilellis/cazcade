package cazcade.hashbo.client.main.menus.add;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.pool.objects.photo.PhotoEditorPanel;
import cazcade.vortex.pool.objects.website.WebsiteEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreateWebsiteCommand extends CreateItemCommand {




    public CreateWebsiteCommand(LiquidURI pool, LSDDictionaryTypes type, Size size, String theme) {
        super(pool, type, size, theme);
    }

    @Override
    public void execute() {
        showEditorPanel(new WebsiteEditorPanel(createEntityWithDefaultView()));
    }


    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
