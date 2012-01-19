package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainSocialDAO {
    LSDTransferEntity followResourceTX(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LiquidRequestDetailLevel detail,
                                       boolean internal) throws Exception;

    LSDTransferEntity getAliasAsProfileTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, boolean internal,
                                          LiquidRequestDetailLevel detail) throws Exception;

    @Nullable
    Collection<LSDBaseEntity> getRosterNoTX(LiquidURI uri, boolean internal, LiquidSessionIdentifier identity,
                                            LiquidRequestDetailLevel request) throws InterruptedException;

    @Nullable
    Collection<LSDBaseEntity> getRosterNoTX(LiquidUUID target, boolean internal, LiquidSessionIdentifier identity,
                                            LiquidRequestDetailLevel detail) throws InterruptedException;


    @Nonnull
    ChangeReport getUpdateSummaryForAlias(LiquidURI aliasURI, long since) throws InterruptedException;

    boolean isFollowing(LSDPersistedEntity currentAlias, LSDPersistedEntity persistedEntity) throws InterruptedException;

    void recordChat(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LSDBaseEntity entity);

    LSDTransferEntity unfollowResourceTX(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LiquidRequestDetailLevel detail,
                                         boolean internal) throws Exception;
}
