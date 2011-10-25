package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.handler.AdminCommandRequestHandler;
import cazcade.liquid.api.request.AdminCommandRequest;
import org.neo4j.graphdb.Transaction;

import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class AdminCommandHandler extends AbstractDataStoreHandler<AdminCommandRequest> implements AdminCommandRequestHandler {
    private Map<String, AdminCommand> adminCommands;

    @Override
    public AdminCommandRequest handle(AdminCommandRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            String command = request.getArgs()[0];
            AdminCommand adminCommand = adminCommands.get(command);
            if (adminCommand == null) {
                return LiquidResponseHelper.forResourceNotFound("No such command " + command, request);
            } else {
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

    public void setAdminCommands(Map adminCommands) {
        this.adminCommands = adminCommands;
    }

    public Map getAdminCommands() {
        return adminCommands;
    }
}