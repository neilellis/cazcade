package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.VisitPoolRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class VisitPoolHandler extends AbstractDataStoreHandler<VisitPoolRequest> implements VisitPoolRequestHandler {

    @Nonnull
    public VisitPoolRequest handle(@Nonnull final VisitPoolRequest request) throws Exception {
        FountainEntity fountainEntity;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDEntity entity = null;

            if (request.getUri() != null) {
                fountainEntity = fountainNeo.findByURI(request.getUri());
            } else {
                fountainEntity = fountainNeo.findByUUID(request.getTarget());
            }

            if (fountainEntity == null && request.isOrCreate() && !request.getSessionIdentifier().isAnon()) {
                final FountainEntity parentFountainEntity = fountainNeo.findByURI(request.getUri().getParentURI());
                final LiquidURI owner = defaultAndCheckOwner(request, request.getAlias());

                final String name = request.getUri().getLastPathElement();
                final String boardTitle = request.isListed() && (name.startsWith(request.getSessionIdentifier().getName() + "-") || name.startsWith("-")) ? "Untitled" : name;
                final StringBuilder newTitle = new StringBuilder();
                boolean previousCharWhitespace = true;
                for (int i = 0; i < boardTitle.length(); i++) {
                    final char c = boardTitle.charAt(i);
                    if ("._-".indexOf(c) >= 0) {
                        if (!previousCharWhitespace) {
                            newTitle.append(" ");
                        }
                        previousCharWhitespace = true;
                    } else if (Character.isWhitespace(c)) {
                        if (!previousCharWhitespace) {
                            newTitle.append(' ');
                        }
                        previousCharWhitespace = true;
                    } else if (Character.isUpperCase(c)) {
                        if (!previousCharWhitespace) {
                            newTitle.append(' ');
                        }
                        newTitle.append(c);
                        previousCharWhitespace = false;
                    } else {
                        if (previousCharWhitespace) {
                            newTitle.append(Character.toUpperCase(c));
                        } else {
                            newTitle.append(c);
                        }
                        previousCharWhitespace = false;
                    }
                }
                fountainEntity = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentFountainEntity, request.getType(), name, 0.0, 0.0, newTitle.toString(), request.isListed());
                if (request.getPermission() != null) {
                    fountainEntity = fountainNeo.changeNodePermissionNoTx(fountainEntity, request.getSessionIdentifier(), request.getPermission());
                    fountainEntity.assertLatestVersion();
                }
            }

            if (fountainEntity == null) {
                return LiquidResponseHelper.forResourceNotFound("Could not find pool " + request.getUri(), request);
            } else {
                fountainEntity.assertLatestVersion();
                poolDAO.visitNodeNoTx(fountainEntity, request.getSessionIdentifier());
                fountainEntity.assertLatestVersion();
                entity = poolDAO.getPoolAndContentsNoTx(fountainEntity, request.getDetail(), true, ChildSortOrder.AGE, request.isInternal(), request.getSessionIdentifier(), null, null, request.isHistorical());
                final LSDEntity visitor = userDAO.getAliasFromNode(fountainNeo.findByURI(request.getAlias()), request.isInternal(), request.getDetail());
                entity.addSubEntity(LSDAttribute.VISITOR, visitor, true);
                transaction.success();
            }
            if (entity != null) {
                return LiquidResponseHelper.forServerSuccess(request, entity);
            } else {
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