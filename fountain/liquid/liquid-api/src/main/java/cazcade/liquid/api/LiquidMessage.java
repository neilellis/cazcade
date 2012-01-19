package cazcade.liquid.api;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.SerializedRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessage extends Serializable {
    @Nonnull
    SerializedRequest asSerializedRequest();


    /**
     * Makes a copy of this message, but <b>note</b> that the origin is reset to UNASSIGNED.
     *
     * @return a copy of this message.
     */
    @Nonnull
    LiquidMessage copy();

    /**
     * TODO: Deprecate this, it's used by the GWT Bus only.
     *
     * @return
     */
    Collection<LiquidURI> getAffectedEntities();

    @Nonnull
    String getCacheIdentifier();

    LiquidCachingScope getCachingScope();

    @Nonnull
    String getDeduplicationIdentifier();

    LiquidUUID getId();

    @Nonnull
    LiquidMessageType getMessageType();

    LiquidMessageOrigin getOrigin();

    @Nullable
    LSDTransferEntity getRequestEntity();

    @Nullable
    LSDTransferEntity getResponse();

    /**
     * If there is a response entity returns that otherwise returns the original entity from the request.
     */
    @Nullable
    LSDBaseEntity getResponseOrRequestEntity();

    LiquidMessageState getState();

    boolean isCacheable();

    void setCachingScope(LiquidCachingScope cachingScope);

    void setId(LiquidUUID id);

    void setOrigin(LiquidMessageOrigin origin);


    void setResponse(LSDTransferEntity entity);

    void setState(LiquidMessageState status);
}
