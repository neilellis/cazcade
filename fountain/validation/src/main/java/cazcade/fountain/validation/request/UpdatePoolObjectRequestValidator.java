package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectRequestValidator extends AbstractRequestValidator<UpdatePoolObjectRequest> {
    @Nonnull
    private static final Logger log = Logger.getLogger(UpdatePoolObjectRequestValidator.class);

    public void validate(@Nonnull final UpdatePoolObjectRequest request, final ValidationLevel level) {
        log.debug("Validating update pool object request.");
        validPoolObject(request);
        entityValidator.validate(request.getRequestEntity(), level);
    }

}