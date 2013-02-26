/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request.util;

import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Types;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RequestUtil {
    public static boolean positiveResponse(@Nonnull final LiquidRequest response) {
        final Entity responseEntity = response.response();
        return !(responseEntity.is(Types.T_EMPTY_RESULT)
                 || responseEntity.is(Types.T_AUTHORIZATION_DENIAL)
                 || responseEntity.is(Types.T_RESOURCE_NOT_FOUND));
    }
}
