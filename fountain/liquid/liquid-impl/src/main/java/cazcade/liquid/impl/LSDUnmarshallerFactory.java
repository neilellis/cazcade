package cazcade.liquid.impl;

import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDUnmarshallerFactory {
    private Map<String, LSDUnmarshaler> marshallers;

    public void setUnmarshalers(final Map<String, LSDUnmarshaler> marshallers) {
        this.marshallers = marshallers;
    }

    public Map<String, LSDUnmarshaler> getUnmarshalers() {
        return marshallers;
    }
}
