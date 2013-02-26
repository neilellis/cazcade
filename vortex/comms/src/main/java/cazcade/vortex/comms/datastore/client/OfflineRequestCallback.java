/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.CachingScope;
import cazcade.liquid.api.SessionIdentifier;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.StatusCodeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OfflineRequestCallback implements RequestCallback {

    // This String we can save in localStorage.
    private       String            serializedResponse;
    private final String            requestIdentifier;
    private final SessionIdentifier identity;
    private final RequestCallback   callback;
    private final String            applicationVersion;

    public OfflineRequestCallback(final String requestIdentifier, final SessionIdentifier identity, final RequestCallback callback, final String applicationVersion) {
        this.requestIdentifier = requestIdentifier;
        this.identity = identity;
        this.callback = callback;
        this.applicationVersion = applicationVersion;
    }

    @Override
    public void onResponseReceived(final Request request, @Nonnull final Response response) {
        if (response.getStatusCode() == 304 && Storage.isSessionStorageSupported()) {
            final Storage storage = Storage.getSessionStorageIfSupported();
            if (storage.getItem(cacheKey()) != null) {
                callback.onResponseReceived(request, getOldResponse(storage));
                return;
            }
        }
        if (response.getStatusCode() == 304 && Storage.isLocalStorageSupported()) {
            final Storage storage = Storage.getLocalStorageIfSupported();
            if (storage.getItem(cacheKey()) != null) {
                callback.onResponseReceived(request, getOldResponse(storage));
                return;
            }
        }
        if (response.getStatusCode() == 200
            && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE) != null
            && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE).equals(CachingScope.USER.name())) {
            serializedResponse = response.getText();
            if (Storage.isLocalStorageSupported()) {
                final Storage storage = Storage.getLocalStorageIfSupported();
                storage.setItem(cacheKey(), serializedResponse);
            }
        } else if (response.getStatusCode() == 200
                   && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE) != null
                   && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE).equals(CachingScope.SESSION.name())) {
            serializedResponse = response.getText();
            if (Storage.isSessionStorageSupported()) {
                final Storage storage = Storage.getSessionStorageIfSupported();
                storage.setItem(cacheKey(), serializedResponse);
            }
        }
        callback.onResponseReceived(request, response);

    }

    @Nonnull
    private String cacheKey() {
        return identity.name() + ":" + applicationVersion + ":" + requestIdentifier;
    }


    @Override
    public void onError(final Request request, final Throwable exception) {
        if (exception instanceof StatusCodeException) {
            if (Storage.isSessionStorageSupported()) {
                final Storage storage = Storage.getSessionStorageIfSupported();
                if (storage.getItem(cacheKey()) != null) {
                    callback.onResponseReceived(request, getOldResponse(storage));
                }
            } else if (Storage.isLocalStorageSupported()) {
                final Storage storage = Storage.getLocalStorageIfSupported();
                if (storage.getItem(cacheKey()) != null) {
                    callback.onResponseReceived(request, getOldResponse(storage));
                }
            }
        } else {
            callback.onError(request, exception);
        }
    }

    @Nullable Response getOldResponse(@Nonnull final Storage storage) {

        return new Response() {

            @Override
            public String getText() {
                return storage.getItem(cacheKey());
            }

            @Nullable @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return 200;
            }

            @Nullable @Override
            public String getHeadersAsString() {
                return null;
            }

            @Nullable @Override
            public Header[] getHeaders() {
                return null;
            }

            @Nullable @Override
            public String getHeader(final String header) {
                return null;
            }
        };
    }

}