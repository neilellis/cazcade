/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.client;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.SerializedRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

public interface DataStoreServiceAsync {


    void login(String username, String password, AsyncCallback<SessionIdentifier> async);


    void checkUsernameAvailability(String username, AsyncCallback<Boolean> async);

    void register(String fullname, String username, String password, String emailAddress, AsyncCallback<TransferEntity> async);


    void collect(SessionIdentifier identity, ArrayList<String> location, AsyncCallback<ArrayList<SerializedRequest>> async);

    void logout(SessionIdentifier identity, AsyncCallback<Void> async);


    void loginQuick(boolean anon, AsyncCallback<SessionIdentifier> async);


    void checkBoardAvailability(LiquidURI board, AsyncCallback<Boolean> async);

    void process(SerializedRequest request, AsyncCallback<SerializedRequest> async);
}
