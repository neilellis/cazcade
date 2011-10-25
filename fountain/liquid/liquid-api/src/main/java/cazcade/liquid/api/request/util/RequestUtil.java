package cazcade.liquid.api.request.util;

import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

/**
 * @author neilellis@cazcade.com
 */
public class RequestUtil {
    public static boolean positiveResponse(LiquidRequest response) {
        return !(response.getResponse().isA(LSDDictionaryTypes.EMPTY_RESULT) || response.getResponse().isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL) || response.getResponse().isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND));
    }
}
