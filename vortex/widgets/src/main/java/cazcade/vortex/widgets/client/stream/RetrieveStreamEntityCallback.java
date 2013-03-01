/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.common.client.User;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import com.google.gwt.user.client.ui.InsertPanel;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
class RetrieveStreamEntityCallback extends AbstractMessageCallback<AbstractRequest> {
    private final int                      maxRows;
    private final InsertPanel              parentPanel;
    private final LURI                     pool;
    private final VortexThreadSafeExecutor threadSafeExecutor;
    private final boolean                  autoDelete;

    public RetrieveStreamEntityCallback(final int maxRows, final InsertPanel parentPanel, final LURI pool, final VortexThreadSafeExecutor threadSafeExecutor, final boolean autoDelete) {
        super();
        this.maxRows = maxRows;
        this.parentPanel = parentPanel;
        this.pool = pool;
        this.threadSafeExecutor = threadSafeExecutor;
        this.autoDelete = autoDelete;
    }

    @Override
    public void onSuccess(final AbstractRequest original, @Nonnull final AbstractRequest message) {
        final List<TransferEntity> entries = message.response().children(Dictionary.CHILD_A);
        Collections.reverse(entries);
        for (final TransferEntity entry : entries) {
            if (entry.is(Types.T_COMMENT) && entry.$(Dictionary.TEXT_BRIEF) != null && !entry.$(Dictionary.TEXT_BRIEF).isEmpty()) {
                StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new CommentEntryPanel(entry), autoDelete, true);
            } else {
                if (entry.has(Dictionary.SOURCE)) {
                    //TODO: This should all be done on the serverside (see LatestContentFinder).
                    final Entity author = entry.child(Dictionary.AUTHOR_A, true);
                    final boolean isMe = author.$(Dictionary.URI).equals(User.getIdentity().aliasURI().asString());
                    final boolean isAnon = User.isAnonymousAliasURI(author.$(Dictionary.URI));
                    final LURI sourceURI = new LURI(entry.$(Dictionary.SOURCE));
                    final boolean isHere = pool == null || sourceURI.withoutFragmentOrComment()
                                                                    .equals(pool.withoutFragmentOrComment());
                    final boolean expired = entry.published().getTime()
                                            < System.currentTimeMillis() - NotificationPanel.UPDATE_LIEFTIME;

                    if (!isAnon && !expired && !isMe && !isHere && BoardURL.isConvertable(sourceURI)) {
                        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new VortexStatusUpdatePanel(entry, false), autoDelete, true);
                        //  statusUpdateSound.play();
                    }
                }

            }
        }
    }
}
