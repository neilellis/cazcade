/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.api;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public interface FountainDataStore extends ServiceStateMachine {
    @Nonnull <T extends LiquidRequest> T process(T request) throws Exception;
}
