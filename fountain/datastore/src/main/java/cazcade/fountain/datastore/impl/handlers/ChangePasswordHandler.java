package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.ChangePasswordRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.ChangePasswordRequest;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class ChangePasswordHandler extends AbstractDataStoreHandler<ChangePasswordRequest> implements ChangePasswordRequestHandler {
    public static final String HASHED_PASSWORD = LSDAttribute.HASHED_AND_SALTED_PASSWORD.getKeyName();

    public ChangePasswordRequest handle(ChangePasswordRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node node = fountainNeo.findByURI(request.getSessionIdentifier().getUserURL());
            String plainPassword = request.getPassword();
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
            node.setProperty(HASHED_PASSWORD, encryptedPassword);
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.convertNodeToLSD(node, request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

}