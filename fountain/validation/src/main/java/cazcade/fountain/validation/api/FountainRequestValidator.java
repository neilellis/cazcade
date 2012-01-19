package cazcade.fountain.validation.api;

import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public interface FountainRequestValidator<T extends LiquidRequest> {
    void validate(T request, ValidationLevel level);
}
