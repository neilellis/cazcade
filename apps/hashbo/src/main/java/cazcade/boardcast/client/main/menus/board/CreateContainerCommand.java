package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.request.CreatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public abstract class CreateContainerCommand extends AbstractCreateCommand {

    public CreateContainerCommand(final LiquidURI pool, final LSDDictionaryTypes type) {
        super(pool, type);
    }


    @Override
    public void execute() {

        BusFactory.getInstance().send(new CreatePoolRequest(getType(), pool, getInitialName(), getInitialName(), getInitialName(), 200.0, 200.0), new AbstractResponseCallback<CreatePoolRequest>() {
            @Override
            public void onSuccess(final CreatePoolRequest message, final CreatePoolRequest response) {
            }
        });
    }

    @Nonnull
    protected abstract String getInitialName();


}
