/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;


/**
 * @author neilellis@cazcade.com
 */
public interface BuildVersionServiceAsync {
    void getBuildVersion(AsyncCallback<HashMap<String, String>> async);
}
