/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Note that this class may contain unsafe data and may have come from an untrusted source, the conversion to a TransferEntity
 * will strip certain types of HTML data from values in the underlying map during this process.
 *
 * @author neilellis@cazcade.com
 */
public final class SerializedRequest implements Serializable {


    private String                  typeRaw;
    private HashMap<String, String> entityRaw;

    public SerializedRequest(@Nonnull final RequestType type, @Nonnull final TransferEntity entity) {
        typeRaw = type.name().substring(2);
        entityRaw = new HashMap<String, String>(entity.map());
    }

    protected SerializedRequest() {
    }


    @Nonnull
    public TransferEntity getEntity() {
        SimpleEntity<? extends TransferEntity> result = SimpleEntity.fromProperties(entityRaw);
        return result;
    }

    @Nonnull
    public RequestType getType() {
        return RequestType.valueOf("R_"+typeRaw);
    }

    @Override
    public String toString() {
        return "SerializedRequest{" +
               "type='" + typeRaw + '\'' +
               ", entity=" + entityRaw +
               '}';
    }
}
