package cazcade.vortex.bus.server;

import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.impl.UUIDFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author neilellis@cazcade.com
 */
public class UUIDServiceImpl extends RemoteServiceServlet implements cazcade.vortex.bus.client.UUIDService {
    @Nonnull
    public ArrayList<LiquidUUID> getRandomUUIDs(int count) {
        ArrayList<LiquidUUID> list = new ArrayList<LiquidUUID>(count);
        for (int i = 0; i < count; i++) {
            list.add(UUIDFactory.randomUUID());
        }
        return list;
    }
}