package cazcade.boardcast.client;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author neilellis@cazcade.com
 */
public interface BuildVersionServiceAsync {
    void getBuildVersion(AsyncCallback<String> async);
}
