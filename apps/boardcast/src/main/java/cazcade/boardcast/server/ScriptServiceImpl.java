/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.server;

import cazcade.boardcast.client.ScriptService;
import cazcade.liquid.api.SessionIdentifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author neilellis@cazcade.com
 */
public class ScriptServiceImpl extends RemoteServiceServlet implements ScriptService {
    @Override
    public void execute(final SessionIdentifier session, final String script) {

    }
}