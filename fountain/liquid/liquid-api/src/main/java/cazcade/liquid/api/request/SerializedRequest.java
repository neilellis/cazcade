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
 * @author neilellis@cazcade.com
 */
public final class SerializedRequest implements Serializable {

    private static final long serialVersionUID = 8663364255167259991L;

    private String                  type;
    private HashMap<String, String> entity;

    public SerializedRequest(@Nonnull final LiquidRequestType type, @Nonnull final LSDTransferEntity entity) {
        this.type = type.name();
        this.entity = new HashMap<String, String>(entity.getMap());
    }

    public SerializedRequest() {
    }

    @Nonnull
    public LSDTransferEntity getEntity() {
        return LSDSimpleEntity.createFromProperties(entity);
    }

    @Nonnull
    public LiquidRequestType getType() {
        return LiquidRequestType.valueOf(type);
    }

    @Override
    public String toString() {
        return "SerializedRequest{" +
               "type='" + type + '\'' +
               ", entity=" + entity +
               '}';
    }
}
