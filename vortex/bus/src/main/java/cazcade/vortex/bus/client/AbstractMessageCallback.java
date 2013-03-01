/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.gwt.util.client.Config;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractMessageCallback<T extends LiquidMessage> implements MessageCallback<T> {

    public void onSuccess(final T original, final T message) {
    }

    public void onFailure(final T original, @Nonnull final T message) {
        final TransferEntity response = message.response();
        ClientLog.log(response.toString());
        if (Config.debug()) {
            Window.alert(response.has(DESCRIPTION)
                         ? "Server error: " + response.$(DESCRIPTION)
                         : "Server error: " + response.toString());

        }
    }

    public void onException(@Nonnull final T message, @Nonnull final Throwable error) {
        final String msg = "Message "
                           + message.id()
                           + " of type "
                           + message.messageType().name()
                           + " had error "
                           + error.getMessage();
        ClientLog.log(msg, error);
    }
}
