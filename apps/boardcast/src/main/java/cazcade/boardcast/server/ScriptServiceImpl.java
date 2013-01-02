package cazcade.boardcast.server;

import cazcade.boardcast.client.ScriptService;
import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author neilellis@cazcade.com
 */
public class ScriptServiceImpl extends RemoteServiceServlet implements ScriptService {
    @Override
    public void execute(final LiquidSessionIdentifier session, final String script) {

    }
}