/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractResponseCallback<T extends LiquidMessage> implements ResponseCallback<T> {

    public void onSuccess(final T message, final T response) {
    }

    public void onFailure(final T message, @Nonnull final T response) {
        final Entity responseEntity = response.response();
        ClientLog.log(responseEntity.toString());
        if (ClientApplicationConfiguration.isDebug()) {
            if (responseEntity.has$(Dictionary.DESCRIPTION)) {
                Window.alert("Server error: " + responseEntity.$(Dictionary.DESCRIPTION));
            } else {
                Window.alert("Server error: " + responseEntity.toString());
            }

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
