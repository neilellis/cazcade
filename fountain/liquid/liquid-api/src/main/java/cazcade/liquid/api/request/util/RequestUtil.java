package cazcade.liquid.api.request.util;

import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;

/**
 * @author neilellis@cazcade.com
 */
public class RequestUtil {
    public static boolean positiveResponse(LiquidRequest response) {
        final LSDEntity responseEntity = response.getResponse();
        return !(responseEntity.isA(LSDDictionaryTypes.EMPTY_RESULT) || responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL) || responseEntity.isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND));
    }
}
