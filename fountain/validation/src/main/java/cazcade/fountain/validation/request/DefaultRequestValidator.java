package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class DefaultRequestValidator extends AbstractRequestValidator<LiquidRequest> {

    private static final Logger log = Logger.getLogger(DefaultRequestValidator.class);

    public void validate(LiquidRequest request, ValidationLevel level) {
        log.debug("Default validator.");
        if (request.getEntity() != null) {
            entityValidator.validate(request.getEntity(), level);
        }

    }
}