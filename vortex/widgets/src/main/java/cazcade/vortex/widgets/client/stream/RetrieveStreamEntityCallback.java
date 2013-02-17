/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import com.google.gwt.user.client.ui.InsertPanel;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
class RetrieveStreamEntityCallback extends AbstractResponseCallback<AbstractRequest> {
    private final int                      maxRows;
    private final InsertPanel              parentPanel;
    private final LiquidURI                pool;
    private final VortexThreadSafeExecutor threadSafeExecutor;
    private final boolean                  autoDelete;

    public RetrieveStreamEntityCallback(final int maxRows, final InsertPanel parentPanel, final LiquidURI pool, final VortexThreadSafeExecutor threadSafeExecutor, final boolean autoDelete) {
        super();
        this.maxRows = maxRows;
        this.parentPanel = parentPanel;
        this.pool = pool;
        this.threadSafeExecutor = threadSafeExecutor;
        this.autoDelete = autoDelete;
    }

    @Override
    public void onSuccess(final AbstractRequest message, @Nonnull final AbstractRequest response) {
        final List<LSDTransferEntity> entries = response.getResponse().getSubEntities(LSDAttribute.CHILD);
        Collections.reverse(entries);
        for (final LSDTransferEntity entry : entries) {
            if (entry.isA(LSDDictionaryTypes.COMMENT)
                && entry.getAttribute(LSDAttribute.TEXT_BRIEF) != null
                && !entry.getAttribute(LSDAttribute.TEXT_BRIEF).isEmpty()) {
                StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new CommentEntryPanel(entry), autoDelete, true);
            } else {
                if (entry.hasAttribute(LSDAttribute.SOURCE)) {
                    //TODO: This should all be done on the serverside (see LatestContentFinder).
                    final LSDBaseEntity author = entry.getSubEntity(LSDAttribute.AUTHOR, true);
                    final boolean isMe = author.getAttribute(LSDAttribute.URI)
                                               .equals(UserUtil.getIdentity().getAliasURL().asString());
                    final boolean isAnon = UserUtil.isAnonymousAliasURI(author.getAttribute(LSDAttribute.URI));
                    final LiquidURI sourceURI = new LiquidURI(entry.getAttribute(LSDAttribute.SOURCE));
                    final boolean isHere = pool == null || sourceURI.getWithoutFragmentOrComment()
                                                                    .equals(pool.getWithoutFragmentOrComment());
                    final boolean expired = entry.getPublished().getTime()
                                            < System.currentTimeMillis() - NotificationPanel.UPDATE_LIEFTIME;

                    if (!isAnon && !expired && !isMe && !isHere && LiquidBoardURL.isConvertable(sourceURI)) {
                        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new VortexStatusUpdatePanel(entry, false), autoDelete, true);
                        //  statusUpdateSound.play();
                    }
                }

            }
        }
    }
}
