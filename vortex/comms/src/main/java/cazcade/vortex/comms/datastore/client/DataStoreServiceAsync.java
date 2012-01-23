package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.SerializedRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

public interface DataStoreServiceAsync {


    void login(String username, String password, AsyncCallback<LiquidSessionIdentifier> async);


    void checkUsernameAvailability(String username, AsyncCallback<Boolean> async);

    void register(String fullname, String username, String password, String emailAddress, AsyncCallback<LSDTransferEntity> async);


    void collect(LiquidSessionIdentifier identity, ArrayList<String> location, AsyncCallback<ArrayList<SerializedRequest>> async);

    void logout(LiquidSessionIdentifier identity, AsyncCallback<Void> async);


    void loginQuick(boolean anon, AsyncCallback<LiquidSessionIdentifier> async);


    void checkBoardAvailability(LiquidURI board, AsyncCallback<Boolean> async);

    void process(SerializedRequest request, AsyncCallback<SerializedRequest> async);
}
