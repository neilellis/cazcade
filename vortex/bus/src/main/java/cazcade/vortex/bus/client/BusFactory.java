package cazcade.vortex.bus.client;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class BusFactory {

    @Nonnull
    private static final Bus instance = new BusImpl();

    @Nonnull
    public static Bus getInstance() {
        return instance;
    }
}
