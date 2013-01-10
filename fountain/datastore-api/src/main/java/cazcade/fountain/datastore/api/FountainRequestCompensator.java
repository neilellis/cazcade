/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.api;

import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainRequestCompensator<T extends LiquidRequest> {
    @Nullable LiquidRequest compensate(T request);
}
