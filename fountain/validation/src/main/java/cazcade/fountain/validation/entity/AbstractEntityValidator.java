/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation.entity;

import cazcade.fountain.validation.api.TypeValidator;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Entity;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractEntityValidator implements TypeValidator {
    void assertHasKey(@Nonnull final Entity entity, @Nonnull final Attribute key) {
        if (!entity.has(key)) {
            throw new ValidationException("Entity of type " + entity.type() + " did not posses key " + key.getKeyName());
        }
    }
}
