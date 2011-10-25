package cazcade.hashbo.server;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import cazcade.hashbo.client.ScriptService;

/**
 * @author neilellis@cazcade.com
 */
public class ScriptServiceImpl extends RemoteServiceServlet implements ScriptService {
    @Override
    public void execute(LiquidSessionIdentifier session, String script) {

    }
}