package cazcade.fountain.datastore.client.validation;

import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class SecurityValidatorImpl implements SecurityValidator {

    private AuthorizationService authorizationService;

    public LiquidRequest validate(LiquidRequest request) {
        try {
            return authorizationService.authorize(request);
        } catch (AuthorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AuthorizationException(e);
        }

    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
