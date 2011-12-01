package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.CreatePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectRequestValidator extends AbstractRequestValidator<CreatePoolObjectRequest> {
    @Nonnull
    private final static Logger log = Logger.getLogger(CreatePoolObjectRequestValidator.class);

    public void validate(@Nonnull CreatePoolObjectRequest request, ValidationLevel level) {
        log.debug("Validating create pool object request.");
        validPoolObject(request);

        entityValidator.validate(request.getRequestEntity(), level);
    }
}