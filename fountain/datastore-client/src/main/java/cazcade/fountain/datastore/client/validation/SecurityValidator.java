package cazcade.fountain.datastore.client.validation;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public interface SecurityValidator {
    LiquidRequest validate(LiquidRequest request);

}
