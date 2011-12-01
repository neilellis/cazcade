package cazcade.vortex.bus.client.simpleevent;

/**
 * Being totally fed up with GWT's insanely complex event model
 * I decided to write something that actually met my needs without adding
 *
 * @author neilellis@cazcade.com
 */
public class SimpleEventDispatcher {


    public void dispatch(final SimpleEvent event, final String key) {

    }

    public void addListener(final Class<? extends SimpleEvent> type, final String keyPattern, final SimpleEventListener listener) {

    }

    public void addListener(final String keyPattern, final SimpleEventListener listener) {

    }

}
