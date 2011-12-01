package cazcade.vortex.server;

import cazcade.vortex.client.VortexService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.annotation.Nonnull;

public class VortexServiceImpl extends RemoteServiceServlet implements VortexService {
    // Implementation of sample interface method
    @Nonnull
    public String getMessage(final String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}