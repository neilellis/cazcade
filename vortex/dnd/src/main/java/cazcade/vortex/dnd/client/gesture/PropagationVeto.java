package cazcade.vortex.dnd.client.gesture;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * @author neilellis@cazcade.com
 */
public interface PropagationVeto {

    /**
     * If it returns true the event will be accepted, i.e. propagated.
     *
     * @param type the event type
     * @return true to accept
     */
    boolean propagate(DomEvent.Type type);

}
