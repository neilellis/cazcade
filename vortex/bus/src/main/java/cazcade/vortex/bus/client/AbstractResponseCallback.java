package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractResponseCallback<T extends LiquidMessage> implements ResponseCallback<T> {

    public void onSuccess(T message, T response) {
    }

    public void onFailure(T message, T response) {
        ClientLog.log(response.getResponse().toString());
        if (ClientApplicationConfiguration.isDebug()) {
            if (response.getResponse().hasAttribute(LSDAttribute.DESCRIPTION)) {
                Window.alert("Server error: " + response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
            } else {
                Window.alert("Server error: " + response.getResponse().toString());
            }

        }
    }

    public void onException(T message, Throwable error) {
        String msg = "Message " + message.getId() + " of type " + message.getMessageType().name() + " had error " + error.getMessage();
        ClientLog.log(msg, error);
    }
}
