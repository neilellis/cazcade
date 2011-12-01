package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;

public class OfflineRequestBuilder extends RequestBuilder {

    private final LiquidSessionIdentifier identity;
    private final String applicationVersion;

    public OfflineRequestBuilder(final LiquidSessionIdentifier identity, final Method httpMethod, final String url, final String applicationVersion) {
        super(httpMethod, url);
        this.identity = identity;
        this.applicationVersion = applicationVersion;
    }


    @Override
    public Request send() throws RequestException {
        //todo add dates to the since field :-)
        final String requestData = getRequestData();
        if (Storage.isSessionStorageSupported()) {
            final Storage storage = Storage.getSessionStorageIfSupported();
            if (storage.getItem(cacheKey(requestData)) != null) {
                setHeader(DataStoreService.X_VORTEX_SINCE, "-1");
            }
        }
        if (Storage.isLocalStorageSupported()) {
            final Storage storage = Storage.getLocalStorageIfSupported();
            if (storage.getItem(cacheKey(requestData)) != null) {
                setHeader(DataStoreService.X_VORTEX_SINCE, "-1");
            }
        }
        final OfflineRequestCallback requestCallbackWrapper = new
                OfflineRequestCallback(requestData, identity,
                getCallback(), applicationVersion);

        return sendRequest(requestData, requestCallbackWrapper);
    }

    @Nonnull
    private String cacheKey(final String requestData) {
        return identity.getName() + ":" + applicationVersion + ":" + requestData;
    }

}