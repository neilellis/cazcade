/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;

/**
 * @author neilelliz@cazcade.com
 */
public interface DataStoreHandler<T extends LiquidMessage> extends LiquidMessageHandler<T> {}
