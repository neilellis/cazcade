package cazcade.fountain.validation.request;

import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;

import cazcade.common.Logger;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectRequestValidator extends AbstractRequestValidator<UpdatePoolObjectRequest> {
    private final static Logger log = Logger.getLogger(UpdatePoolObjectRequestValidator.class);

    public void validate(UpdatePoolObjectRequest request, ValidationLevel level) {
        log.debug("Validating update pool object request.");
        validPoolObject(request);
        entityValidator.validate(request.getEntity(), level);
    }

}