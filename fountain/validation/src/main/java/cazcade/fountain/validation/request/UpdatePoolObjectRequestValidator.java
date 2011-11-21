package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectRequestValidator extends AbstractRequestValidator<UpdatePoolObjectRequest> {
    private final static Logger log = Logger.getLogger(UpdatePoolObjectRequestValidator.class);

    public void validate(UpdatePoolObjectRequest request, ValidationLevel level) {
        log.debug("Validating update pool object request.");
        validPoolObject(request);
        entityValidator.validate(request.getRequestEntity(), level);
    }

}