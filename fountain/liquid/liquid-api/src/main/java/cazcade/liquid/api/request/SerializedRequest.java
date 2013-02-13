/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Note that this class may contain unsafe data and may have come from an untrusted source, the conversion to a LSDTransferEntity
 * will strip certain types of HTML data from values in the underlying map during this process.
 *
 * @author neilellis@cazcade.com
 */
public final class SerializedRequest implements Serializable {


    private String                  typeRaw;
    private HashMap<String, String> entityRaw;

    public SerializedRequest(@Nonnull final LiquidRequestType type, @Nonnull final LSDTransferEntity entity) {
        typeRaw = type.name();
        entityRaw = new HashMap<String, String>(entity.getMap());
    }

    protected SerializedRequest() {
    }


    @Nonnull
    public LSDTransferEntity getEntity() {
        return LSDSimpleEntity.createFromProperties(entityRaw);
    }

    @Nonnull
    public LiquidRequestType getType() {
        return LiquidRequestType.valueOf(typeRaw);
    }

    @Override
    public String toString() {
        return "SerializedRequest{" +
               "type='" + typeRaw + '\'' +
               ", entity=" + entityRaw +
               '}';
    }
}
