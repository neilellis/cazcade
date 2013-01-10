/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SearchRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.SearchRequest;

import javax.annotation.Nonnull;


/**
 * @author neilelliz@cazcade.com
 */
public class SearchHandler extends AbstractDataStoreHandler<SearchRequest> implements SearchRequestHandler {
    @Nonnull
    public SearchRequest handle(@Nonnull final SearchRequest request) throws InterruptedException {
        final LSDTransferEntity searchResultEntity = fountainNeo.freeTextSearch(request.getSearchText(), request.getDetail(), request
                .isInternal());
        return LiquidResponseHelper.forServerSuccess(request, searchResultEntity);
    }
}