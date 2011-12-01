package cazcade.vortex.bus.server;

import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.impl.UUIDFactory;
import cazcade.vortex.bus.client.UUIDService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author neilellis@cazcade.com
 */
public class UUIDServiceImpl extends RemoteServiceServlet implements UUIDService {
    @Nonnull
    public ArrayList<LiquidUUID> getRandomUUIDs(final int count) {
        final ArrayList<LiquidUUID> list = new ArrayList<LiquidUUID>(count);
        for (int i = 0; i < count; i++) {
            list.add(UUIDFactory.randomUUID());
        }
        return list;
    }
}