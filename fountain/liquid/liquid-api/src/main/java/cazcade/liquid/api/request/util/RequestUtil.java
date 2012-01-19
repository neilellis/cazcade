package cazcade.liquid.api.request.util;

import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RequestUtil {
    public static boolean positiveResponse(@Nonnull final LiquidRequest response) {
        final LSDBaseEntity responseEntity = response.getResponse();
        return !(responseEntity.isA(LSDDictionaryTypes.EMPTY_RESULT) || responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL
                                                                                          ) || responseEntity.isA(
                LSDDictionaryTypes.RESOURCE_NOT_FOUND
                                                                                                                 ));
    }
}
