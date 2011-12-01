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

    LiquidUUID getId();

    void setId(LiquidUUID id);

    @Nullable
    LSDTransferEntity getRequestEntity();

    /**
     * TODO: Deprecate this, it's used by the GWT Bus only.
     *
     * @return
     */
    Collection<LiquidURI> getAffectedEntities();

    LiquidMessageOrigin getOrigin();

    void setOrigin(LiquidMessageOrigin origin);

    @Nonnull
    LiquidMessageType getMessageType();


    /**
     * Makes a copy of this message, but <b>note</b> that the origin is reset to UNASSIGNED.
     *
     * @return a copy of this message.
     */
    @Nonnull
    LiquidMessage copy();

    LiquidMessageState getState();

    void setState(LiquidMessageState status);


    void setResponse(LSDTransferEntity entity);

    @Nullable
    LSDTransferEntity getResponse();

    @Nonnull
    String getCacheIdentifier();

    boolean isCacheable();

    LiquidCachingScope getCachingScope();

    void setCachingScope(LiquidCachingScope cachingScope);

    @Nonnull
    String getDeduplicationIdentifier();

    /**
     * If there is a response entity returns that otherwise returns the original entity from the request.
     */
    @Nullable
    LSDBaseEntity getResponseOrRequestEntity();


    @Nonnull
    SerializedRequest asSerializedRequest();
}
