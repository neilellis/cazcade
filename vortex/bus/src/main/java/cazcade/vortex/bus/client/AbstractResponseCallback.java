package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
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
        final LSDBaseEntity responseEntity = response.getResponse();
        ClientLog.log(responseEntity.toString());
        if (ClientApplicationConfiguration.isDebug()) {
            if (responseEntity.hasAttribute(LSDAttribute.DESCRIPTION)) {
                Window.alert("Server error: " + responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
            } else {
                Window.alert("Server error: " + responseEntity.toString());
            }

        }
    }

    public void onException(@Nonnull final T message, @Nonnull final Throwable error) {
        final String msg = "Message " + message.getId() + " of type " + message.getMessageType().name() + " had error " + error.getMessage();
        ClientLog.log(msg, error);
    }
}
