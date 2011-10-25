package cazcade.vortex.gwt.util.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public interface ClientLogServiceAsync {

    void log(Throwable t, String log, AsyncCallback<Void> async);
}
