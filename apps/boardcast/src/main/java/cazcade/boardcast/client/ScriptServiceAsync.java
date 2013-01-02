package cazcade.boardcast.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public interface ScriptServiceAsync {
    void execute(LiquidSessionIdentifier session, String script, AsyncCallback<Void> async);
}
