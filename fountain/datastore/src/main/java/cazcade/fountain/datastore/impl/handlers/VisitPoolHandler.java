/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidPermissionChangeType;
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
        LSDPersistedEntity pool;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDTransferEntity entity = null;

            if (request.hasUri()) {
                pool = fountainNeo.findByURI(request.getUri());
            }
            else {
                pool = fountainNeo.findByUUID(request.getTarget());
            }

            if (pool == null && request.isOrCreate() && !request.getSessionIdentifier().isAnon()) {
                final LSDPersistedEntity parentPersistedEntity = fountainNeo.findByURIOrFail(request.getUri().getParentURI());
                final LiquidURI owner = defaultAndCheckOwner(request, request.getAlias());

                final String name = request.getUri().getLastPathElement();
                assert name != null;
                final String boardTitle = name.startsWith(request.getSessionIdentifier().getName() + "-") || name.startsWith("-")
                                          ? "Untitled"
                                          : name;
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
                pool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentPersistedEntity, request.getType(), name, 0.0, 0.0, newTitle
                        .toString(), request.isListed());

                final LiquidPermissionChangeType requestPermission = request.getPermission();
                if (requestPermission != null) {
                    pool = fountainNeo.changeNodePermissionNoTx(pool, request.getSessionIdentifier(), requestPermission);
                    pool.assertLatestVersion();
                }
            }

            if (pool == null) {
                return LiquidResponseHelper.forResourceNotFound("Could not find pool " + request.getUri(), request);
            }
            else {
                pool.assertLatestVersion();
                poolDAO.visitNodeNoTx(pool, request.getSessionIdentifier());
                pool.assertLatestVersion();
                if (request.hasDescription()) { pool.setAttribute(LSDAttribute.DESCRIPTION, request.getDescription()); }
                if (request.hasImageUrl()) { pool.setAttribute(LSDAttribute.IMAGE_URL, request.getImageUrl()); }
                entity = poolDAO.getPoolAndContentsNoTx(pool, request.getDetail(), true, ChildSortOrder.AGE, request.isInternal(), request
                        .getSessionIdentifier(), null, null, request.isHistorical());
                final LSDTransferEntity visitor = userDAO.getAliasFromNode(fountainNeo.findByURIOrFail(request.getAlias()), request.isInternal(), request
                        .getDetail());
                entity.addSubEntity(LSDAttribute.VISITOR, visitor, true);
                transaction.success();
            }
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}