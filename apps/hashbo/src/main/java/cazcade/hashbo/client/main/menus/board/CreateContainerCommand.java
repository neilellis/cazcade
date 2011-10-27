package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.request.CreatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import com.google.gwt.user.client.Command;

/**
 * @author neilellis@cazcade.com
 */
public abstract class CreateContainerCommand extends AbstractCreateCommand implements Command {

    public CreateContainerCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }


    @Override
    public void execute() {

        BusFactory.getInstance().send(new CreatePoolRequest(getType(), pool, getInitialName(), getInitialName(), getInitialName(), 200.0, 200.0), new AbstractResponseCallback<CreatePoolRequest>() {
            @Override
            public void onSuccess(CreatePoolRequest message, CreatePoolRequest response) {
            }
        });
    }

    protected abstract String getInitialName();


}
