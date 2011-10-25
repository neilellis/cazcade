package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidUUID;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;


/**
 * @author neilellis@cazcade.com
 */
public interface UUIDServiceAsync {

    void getRandomUUIDs(int count, AsyncCallback<ArrayList<LiquidUUID>> async);
}
