/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class SimpleEntityFactory<T extends TransferEntity<T>> implements EntityFactory<T> {
    @Nonnull
    public T create(@Nonnull final Map<String, String> properties, final boolean dotPrefixed) {
        final Map<String, String> lsdProperties = new HashMap<String, String>();
        if (dotPrefixed) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith(".")) {
                    lsdProperties.put(key.substring(1), entry.getValue());
                }
            }
        } else {
            lsdProperties.putAll(properties);
        }
        SimpleEntity<? extends TransferEntity> result = SimpleEntity.createFromProperties(lsdProperties);
        return (T) result;
    }

    @Nonnull
    public T create(@Nonnull final LiquidUUID uuid) {
        final SimpleEntity<? extends TransferEntity> structuredPropertyLSDEntity = SimpleEntity.createEmpty();
        structuredPropertyLSDEntity.$(Dictionary.ID, uuid.toString());
        structuredPropertyLSDEntity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        return (T) structuredPropertyLSDEntity;
    }

    @Nonnull
    public T createFromServletProperties(@Nonnull final Map<String, String[]> properties) {
        final Map<String, String> lsdProperties = new HashMap<String, String>();
        for (final Map.Entry<String, String[]> entry : properties.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(".")) {
                lsdProperties.put(key.substring(1), entry.getValue()[0]);
            }
        }
        SimpleEntity<? extends TransferEntity> result = SimpleEntity.createFromProperties(lsdProperties);
        return (T) result;
    }
}
