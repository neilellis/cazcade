/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RetrieveDictionaryRequest extends AbstractRetrievalRequest {
    public RetrieveDictionaryRequest(@Nonnull final Category category) {
        super();
        setCategory(category);
    }

    public RetrieveDictionaryRequest() {
        super();
    }

    public RetrieveDictionaryRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveDictionaryRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_DICTIONARY;
    }
}
