package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.VisitPoolRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class VisitPoolHandler extends AbstractDataStoreHandler<VisitPoolRequest> implements VisitPoolRequestHandler {
    @Nonnull
    public VisitPoolRequest handle(@Nonnull final VisitPoolRequest request) throws Exception {
        LSDPersistedEntity persistedEntity;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDTransferEntity entity = null;

            if (request.getUri() != null) {
                persistedEntity = fountainNeo.findByURI(request.getUri());
            }
            else {
                persistedEntity = fountainNeo.findByUUID(request.getTarget());
            }

            if (persistedEntity == null && request.isOrCreate() && !request.getSessionIdentifier().isAnon()) {
                final LSDPersistedEntity parentPersistedEntity = fountainNeo.findByURI(request.getUri().getParentURI());
                final LiquidURI owner = defaultAndCheckOwner(request, request.getAlias());

                final String name = request.getUri().getLastPathElement();
                final String boardTitle = request.isListed() && (name.startsWith(request.getSessionIdentifier().getName() + "-") ||
                                                                 name.startsWith("-")) ? "Untitled" : name;
                final StringBuilder newTitle = new StringBuilder();
                boolean previousCharWhitespace = true;
                for (int i = 0; i < boardTitle.length(); i++) {
                    final char c = boardTitle.charAt(i);
                    if ("._-".indexOf(c) >= 0) {
                        if (!previousCharWhitespace) {
                            newTitle.append(" ");
                        }
                        previousCharWhitespace = true;
                    }
                    else if (Character.isWhitespace(c)) {
                        if (!previousCharWhitespace) {
                            newTitle.append(' ');
                        }
                        previousCharWhitespace = true;
                    }
                    else if (Character.isUpperCase(c)) {
                        if (!previousCharWhitespace) {
                            newTitle.append(' ');
                        }
                        newTitle.append(c);
                        previousCharWhitespace = false;
                    }
                    else {
                        if (previousCharWhitespace) {
                            newTitle.append(Character.toUpperCase(c));
                        }
                        else {
                            newTitle.append(c);
                        }
                        previousCharWhitespace = false;
                    }
                }
                persistedEntity = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentPersistedEntity,
                                                         request.getType(), name, 0.0, 0.0, newTitle.toString(), request.isListed()
                                                        );
                if (request.getPermission() != null) {
                    persistedEntity = fountainNeo.changeNodePermissionNoTx(persistedEntity, request.getSessionIdentifier(),
                                                                           request.getPermission()
                                                                          );
                    persistedEntity.assertLatestVersion();
                }
            }

            if (persistedEntity == null) {
                return LiquidResponseHelper.forResourceNotFound("Could not find pool " + request.getUri(), request);
            }
            else {
                persistedEntity.assertLatestVersion();
                poolDAO.visitNodeNoTx(persistedEntity, request.getSessionIdentifier());
                persistedEntity.assertLatestVersion();
                entity = poolDAO.getPoolAndContentsNoTx(persistedEntity, request.getDetail(), true, ChildSortOrder.AGE,
                                                        request.isInternal(), request.getSessionIdentifier(), null, null,
                                                        request.isHistorical()
                                                       );
                final LSDTransferEntity visitor = userDAO.getAliasFromNode(fountainNeo.findByURI(request.getAlias()),
                                                                           request.isInternal(), request.getDetail()
                                                                          );
                entity.addSubEntity(LSDAttribute.VISITOR, visitor, true);
                transaction.success();
            }
            if (entity != null) {
                return LiquidResponseHelper.forServerSuccess(request, entity);
            }
            else {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}