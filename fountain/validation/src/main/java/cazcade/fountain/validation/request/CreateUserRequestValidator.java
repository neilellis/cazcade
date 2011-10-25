package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.request.CreateUserRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateUserRequestValidator extends AbstractRequestValidator<CreateUserRequest> {
    private final static Logger log = Logger.getLogger(CreateUserRequestValidator.class);

    public void validate(CreateUserRequest request, ValidationLevel level) {
        log.debug("Validating create user request.");
        if (!request.getEntity().isA(LSDDictionaryTypes.USER)) {
            throw new ValidationException("The entity supplied is not a user entity.");
        }
        entityValidator.validate(request.getEntity(), level);
        if (!request.getEntity().hasAttribute(LSDAttribute.PLAIN_PASSWORD)) {
            throw new ValidationException("You cannot create a new user without supplying the plain text password.");
        }

    }
}
