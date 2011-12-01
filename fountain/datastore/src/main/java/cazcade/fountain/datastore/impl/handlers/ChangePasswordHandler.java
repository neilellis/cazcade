package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.ChangePasswordRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.ChangePasswordRequest;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ChangePasswordHandler extends AbstractDataStoreHandler<ChangePasswordRequest> implements ChangePasswordRequestHandler {
    public static final String HASHED_PASSWORD = LSDAttribute.HASHED_AND_SALTED_PASSWORD.getKeyName();

    @Nonnull
    public ChangePasswordRequest handle(@Nonnull ChangePasswordRequest request) throws Exception {
        Transaction transaction = fountainNeo.beginTx();
        try {
            final LiquidURI userURL = request.getSessionIdentifier().getUserURL();
            Node node = fountainNeo.findByURI(userURL);
            if (node == null) {
                throw new AuthorizationException("No such user " + userURL);
            }
            if (request.getChangePasswordSecurityHash() != null) {
                if (!userDAO.confirmHash(userURL, request.getChangePasswordSecurityHash())) {
                    throw new AuthorizationException("Could not authorize changing of password for %s - hash didn't match.", userURL);
                }
            }
            if (request.getPassword() == null) {
                userDAO.sendPasswordChangeRequest(userURL);
                transaction.success();
                return LiquidResponseHelper.forServerSuccess(request, node.convertNodeToLSD(LiquidRequestDetailLevel.MINIMAL, request.isInternal()));
            } else {
                String plainPassword = request.getPassword();
                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                node.setProperty(LSDAttribute.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                transaction.success();
                return LiquidResponseHelper.forServerSuccess(request, node.convertNodeToLSD(request.getDetail(), request.isInternal()));
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

}