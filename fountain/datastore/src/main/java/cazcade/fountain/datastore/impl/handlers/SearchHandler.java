/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SearchRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.SearchRequest;

import javax.annotation.Nonnull;


/**
 * @author neilelliz@cazcade.com
 */
public class SearchHandler extends AbstractDataStoreHandler<SearchRequest> implements SearchRequestHandler {
    @Nonnull
    public SearchRequest handle(@Nonnull final SearchRequest request) throws InterruptedException {
        final TransferEntity searchResultEntity = neo.freeTextSearch(request.getSearchText(), request.detail(), request.internal());
        return LiquidResponseHelper.forServerSuccess(request, searchResultEntity);
    }
}