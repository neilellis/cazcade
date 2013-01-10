/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.client.validation;

import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nullable;

/**
 * @author neilelliz@cazcade.com
 */
public interface SecurityValidator {
    @Nullable LiquidRequest validate(LiquidRequest request);
}
