package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import com.google.gwt.user.client.ui.InsertPanel;

import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
class RetrieveStreamEntityCallback extends AbstractResponseCallback<AbstractRequest> {
    private FormatUtil features;
    private int maxRows;
    private InsertPanel parentPanel;
    private LiquidURI pool;
    private VortexThreadSafeExecutor threadSafeExecutor;
    private boolean autoDelete;

    public RetrieveStreamEntityCallback(FormatUtil features, int maxRows, InsertPanel parentPanel, LiquidURI pool, VortexThreadSafeExecutor threadSafeExecutor, boolean autoDelete) {
        this.features = features;
        this.maxRows = maxRows;
        this.parentPanel = parentPanel;
        this.pool = pool;
        this.threadSafeExecutor = threadSafeExecutor;
        this.autoDelete = autoDelete;
    }

    @Override
    public void onSuccess(AbstractRequest message, AbstractRequest response) {
        final List<LSDEntity> entries = response.getResponse().getSubEntities(LSDAttribute.CHILD);
        Collections.reverse(entries);
        for (LSDEntity entry : entries) {
            if (entry.isA(LSDDictionaryTypes.COMMENT)
                    && entry.getAttribute(LSDAttribute.TEXT_BRIEF) != null && !entry.getAttribute(LSDAttribute.TEXT_BRIEF).isEmpty()) {
                StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new CommentEntryPanel(entry, features), autoDelete);
            } else {
                if (entry.hasAttribute(LSDAttribute.SOURCE)) {
                    //TODO: This should all be done on the serverside (see LatestContentFinder).
                    final boolean isMe = entry.getSubEntity(LSDAttribute.AUTHOR, false).getAttribute(LSDAttribute.URI).equals(UserUtil.getIdentity().getAliasURL().asString());
                    final boolean isAnon = UserUtil.isAnonymousAliasURI(entry.getSubEntity(LSDAttribute.AUTHOR, false).getAttribute(LSDAttribute.URI));
                    final LiquidURI sourceURI = new LiquidURI(entry.getAttribute(LSDAttribute.SOURCE));
                    final boolean isHere = sourceURI.getWithoutFragmentOrComment().equals(pool.getWithoutFragmentOrComment());
                    final boolean expired = entry.getPublished().getTime() < System.currentTimeMillis() - NotificationPanel.UPDATE_LIEFTIME;

                    if (!isAnon && !expired && !isMe && !isHere && LiquidBoardURL.isConvertable(sourceURI)) {
                        StreamUtil.addStreamEntry(maxRows, parentPanel, threadSafeExecutor, new VortexStatusUpdatePanel(entry, features), autoDelete);
                        //  statusUpdateSound.play();
                    }
                }

            }
        }
    }
}
