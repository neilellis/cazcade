package cazcade.vortex.gwt.util.server;

import cazcade.common.Logger;
import cazcade.vortex.gwt.util.client.ClientLogService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ClientLogServiceImpl extends RemoteServiceServlet implements ClientLogService {
    @Nonnull
    private final static Logger log = Logger.getLogger(ClientLogServiceImpl.class);

    @Override
    public void log(@Nonnull Throwable t, String logStr) {
        log.clearContext();
        log.addContext(logStr);
        log.error(t);
        log.clearContext();
    }
}