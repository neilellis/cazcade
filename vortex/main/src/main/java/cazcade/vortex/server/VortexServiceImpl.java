package cazcade.vortex.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import cazcade.vortex.client.VortexService;

public class VortexServiceImpl extends RemoteServiceServlet implements VortexService {
    // Implementation of sample interface method
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}