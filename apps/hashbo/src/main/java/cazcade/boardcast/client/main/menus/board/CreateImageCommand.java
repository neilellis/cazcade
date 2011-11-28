package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.gwt.util.client.analytics.Track;

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

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Image");

    }
}
