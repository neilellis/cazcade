package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.LiquidCachingScope;
import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class OfflineRequestCallback implements RequestCallback {

    // This String we can save in localStorage.
    private String serializedResponse;
    private String requestIdentifier;
    private LiquidSessionIdentifier identity;
    private RequestCallback callback;
    private String applicationVersion;

    public OfflineRequestCallback(String requestIdentifier, LiquidSessionIdentifier identity, RequestCallback callback, String applicationVersion) {
        this.requestIdentifier = requestIdentifier;
        this.identity = identity;
        this.callback = callback;
        this.applicationVersion = applicationVersion;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
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
        if (response.getStatusCode() == 200 && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE) != null && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE).equals(LiquidCachingScope.USER.name())) {
            serializedResponse = response.getText();
            if (Storage.isLocalStorageSupported()) {
                final Storage storage = Storage.getLocalStorageIfSupported();
                storage.setItem(cacheKey(), serializedResponse);
            }
        } else if (response.getStatusCode() == 200 && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE) != null && response.getHeader(DataStoreService.X_VORTEX_CACHE_SCOPE).equals(LiquidCachingScope.SESSION.name())) {
            serializedResponse = response.getText();
            if (Storage.isSessionStorageSupported()) {
                final Storage storage = Storage.getSessionStorageIfSupported();
                storage.setItem(cacheKey(), serializedResponse);
            }
        }
        callback.onResponseReceived(request, response);

    }

    private String cacheKey() {
        return identity.getName()+":"+applicationVersion+":"+requestIdentifier;
    }


    @Override
    public void onError(Request request, Throwable exception) {
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

    Response getOldResponse(final Storage storage) {

        return new Response() {

            @Override
            public String getText() {
                return storage.getItem(cacheKey());
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return 200;
            }

            @Override
            public String getHeadersAsString() {
                return null;
            }

            @Override
            public Header[] getHeaders() {
                return null;
            }

            @Override
            public String getHeader(String header) {
                return null;
            }
        };
    }

}