package cazcade.vortex.bus.client.simpleevent;

import cazcade.vortex.bus.client.simpleevent.SimpleEvent;

/**
 * Being totally fed up with GWT's insanely complex event model
 * I decided to write something that actually met my needs without adding
 *
 * @author neilellis@cazcade.com
 */
public class SimpleEventDispatcher {



    public void dispatch(SimpleEvent event, String key) {

    }

    public void addListener(Class<? extends SimpleEvent> type, String keyPattern, SimpleEventListener listener) {

    }

    public void addListener( String keyPattern,SimpleEventListener listener) {

    }

}
