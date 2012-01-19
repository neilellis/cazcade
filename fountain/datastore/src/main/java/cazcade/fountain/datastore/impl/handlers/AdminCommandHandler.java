package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.handler.AdminCommandRequestHandler;
import cazcade.liquid.api.request.AdminCommandRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class AdminCommandHandler extends AbstractDataStoreHandler<AdminCommandRequest> implements AdminCommandRequestHandler {
    private Map<String, AdminCommand> adminCommands;

    @Nonnull
    @Override
    public AdminCommandRequest handle(@Nonnull final AdminCommandRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final String command = request.getArgs()[0];
            final AdminCommand adminCommand = adminCommands.get(command);
            if (adminCommand == null) {
                return LiquidResponseHelper.forResourceNotFound("No such command " + command, request);
            }
            else {
                adminCommand.execute(request.getArgs(), fountainNeo);
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

    public Map getAdminCommands() {
        return adminCommands;
    }

    public void setAdminCommands(final Map adminCommands) {
        this.adminCommands = adminCommands;
    }
}