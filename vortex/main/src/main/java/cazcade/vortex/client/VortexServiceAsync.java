package cazcade.vortex.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VortexServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);
}
