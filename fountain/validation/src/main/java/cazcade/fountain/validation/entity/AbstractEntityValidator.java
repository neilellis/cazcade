package cazcade.fountain.validation.entity;

import cazcade.fountain.validation.api.TypeValidator;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractEntityValidator implements TypeValidator {

    void assertHasKey(@Nonnull final LSDEntity entity, @Nonnull final LSDAttribute key) {
        if (!entity.hasAttribute(key)) {
            throw new ValidationException("Entity of type " + entity.getTypeDef() + " did not posses key " + key.getKeyName());
        }

    }
}
