/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.CreatePoolRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public abstract class CreateContainerCommand extends AbstractCreateCommand {

    public CreateContainerCommand(final LURI pool, final Types type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        CreatePoolRequest request = new CreatePoolRequest(getType(), pool, getInitialName(), getInitialName(), getInitialName(), 200.0, 200.0);
        Bus.get().send(request, new AbstractMessageCallback<CreatePoolRequest>() {
            @Override
            public void onSuccess(final CreatePoolRequest original, final CreatePoolRequest message) {
            }
        });
    }

    @Nonnull
    protected abstract String getInitialName();
}
