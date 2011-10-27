package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.pool.objects.youtube.YouTubeEditorPanel;

/**
 * @author neilellis@cazcade.com
 */
public class CreateYouTubeCommand extends CreateItemCommand {
    public CreateYouTubeCommand(LiquidURI pool, LSDDictionaryTypes type, Size size, String theme) {
        super(pool, type, size, theme);
    }


    @Override
    public void execute() {
        showEditorPanel(new YouTubeEditorPanel(createEntityWithDefaultView()));
    }


    @Override
    protected void buildEntity(final BuildCallback onBuilt) {

    }
}
