package cazcade.vortex.bus.client;

/**
 * @author neilellis@cazcade.com
 */
public class BusFactory {

    private static final Bus instance= new BusImpl();

    public static Bus getInstance() {
        return  instance;
    }
}
