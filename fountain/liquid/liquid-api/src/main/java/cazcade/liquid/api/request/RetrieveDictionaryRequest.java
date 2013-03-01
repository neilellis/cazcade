/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.lsd.TransferEntity;

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

    public RetrieveDictionaryRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveDictionaryRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_RETRIEVE_DICTIONARY;
    }
}
