/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.VisitPoolRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class VisitPoolHandler extends AbstractDataStoreHandler<VisitPoolRequest> implements VisitPoolRequestHandler {
    @Nonnull
    public VisitPoolRequest handle(@Nonnull final VisitPoolRequest req) throws Exception {
        PersistedEntity pool;
        final Transaction transaction = neo.beginTx();
        try {
            TransferEntity entity = null;

            if (req.hasUri()) {
                pool = neo.find(req.uri());
            } else {
                pool = neo.find(req.getTarget());
            }

            if (pool == null && req.isOrCreate() && !req.session().anon()) {
                final LiquidURI owner = defaultAndCheckOwner(req, req.alias());

                final String name = req.uri().lastPath();
                assert name != null;
                final String boardTitle = name.startsWith(req.session().name() + "-") || name.startsWith("-") ? "Untitled" : name;
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
                pool = poolDAO.createPoolNoTx(req.session(), owner, neo.findOrFail(req.uri()
                                                                                      .parent()), req.type(), name, 0.0, 0.0, newTitle
                        .toString(), req.listed());
                if (req.hasDescription()) { pool.$(Dictionary.DESCRIPTION, req.description()); }
                if (req.hasImageUrl()) { pool.$(Dictionary.IMAGE_URL, req.imageUrl()); }
                if (req.permission() != null) {
                    pool = neo.changeNodePermissionNoTx(pool, req.session(), req.permission());
                    pool.assertLatestVersion();
                }
            }

            if (pool == null) {
                return LiquidResponseHelper.forResourceNotFound("Could not find pool " + req.uri(), req);
            } else {
                pool.assertLatestVersion();
                poolDAO.visitNodeNoTx(pool, req.session());
                pool.assertLatestVersion();
                entity = poolDAO.getPoolAndContentsNoTx(pool, req.detail(), true, ChildSortOrder.AGE, req.internal(), req.session(), null, null, req
                        .historical());
                entity.child(Dictionary.VISITOR_A, userDAO.getAliasFromNode(neo.findOrFail(req.alias()), req.internal(), req.detail()), true);
                transaction.success();
            }
            return LiquidResponseHelper.forServerSuccess(req, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}