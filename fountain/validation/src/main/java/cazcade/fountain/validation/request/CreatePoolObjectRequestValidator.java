package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.CreatePoolObjectRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectRequestValidator extends AbstractRequestValidator<CreatePoolObjectRequest> {
    private final static Logger log = Logger.getLogger(CreatePoolObjectRequestValidator.class);

    public void validate(CreatePoolObjectRequest request, ValidationLevel level) {
        log.debug("Validating create pool object request.");
        validPoolObject(request);

        entityValidator.validate(request.getRequestEntity(), level);
    }
}