package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.gwt.util.client.analytics.Track;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CreateChecklistCommand extends CreateContainerCommand {
    @Nonnull
    @Override
    protected String getInitialName() {
        return "checklist" + System.currentTimeMillis();
    }

    public CreateChecklistCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        super.execute();
        Track.getInstance().trackEvent("Add", "Add Checklist");

    }
}
