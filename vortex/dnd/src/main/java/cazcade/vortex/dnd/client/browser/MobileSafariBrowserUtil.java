package cazcade.vortex.dnd.client.browser;

/**
 * @author neilellis@cazcade.com
 */
public class MobileSafariBrowserUtil extends SafariBrowserUtil {

    @Override
    public boolean isTouchEnabled() {
        return true;
    }
}
