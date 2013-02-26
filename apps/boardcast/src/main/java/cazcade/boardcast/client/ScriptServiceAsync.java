/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import cazcade.liquid.api.SessionIdentifier;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public interface ScriptServiceAsync {
    void execute(SessionIdentifier session, String script, AsyncCallback<Void> async);
}
