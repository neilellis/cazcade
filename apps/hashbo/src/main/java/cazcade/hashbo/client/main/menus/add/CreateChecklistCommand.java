package cazcade.hashbo.client.main.menus.add;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

/**
 * @author neilellis@cazcade.com
 */
public class CreateChecklistCommand extends CreateContainerCommand {
    @Override
    protected String getInitialName() {
        return "checklist" + System.currentTimeMillis();
    }

    public CreateChecklistCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }
}
