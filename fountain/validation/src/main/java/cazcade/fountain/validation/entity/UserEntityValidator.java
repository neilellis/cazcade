package cazcade.fountain.validation.entity;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

/**
 * @author neilelliz@cazcade.com
 */
public class UserEntityValidator extends AbstractEntityValidator {
    public void validate(LSDEntity entity) {
        assertHasKey(entity, LSDAttribute.FULL_NAME);
        assertHasKey(entity, LSDAttribute.EMAIL_ADDRESS);
    }
}
