package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.storage.client.Storage;

public class OfflineRequestBuilder extends RequestBuilder {

    private LiquidSessionIdentifier identity;
    private String applicationVersion;

    public OfflineRequestBuilder(LiquidSessionIdentifier identity, Method httpMethod, String url, String applicationVersion) {
        super(httpMethod, url);
        this.identity = identity;
        this.applicationVersion = applicationVersion;
    }


    @Override
    public Request send() throws RequestException {
        //todo add dates to the since field :-)
        String requestData = super.getRequestData();
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
        OfflineRequestCallback requestCallbackWrapper = new
                OfflineRequestCallback(requestData, identity,
                super.getCallback(), applicationVersion);

        return super.sendRequest(requestData, requestCallbackWrapper);
    }

    private String cacheKey(String requestData) {
        return identity.getName()+":"+applicationVersion+":"+requestData;
    }

}