package cazcade.vortex.gwt.util.server;

import cazcade.common.Logger;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import cazcade.vortex.gwt.util.client.ClientLogService;

/**
 * @author neilellis@cazcade.com
 */
public class ClientLogServiceImpl extends RemoteServiceServlet implements ClientLogService {
    private final static Logger log = Logger.getLogger(ClientLogServiceImpl.class);
    @Override
    public void log(Throwable t, String logStr) {
        log.clearContext();
        log.addContext(logStr);
        log.error(t);
        log.clearContext();
    }
}