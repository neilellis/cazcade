package cazcade.liquid.impl;

import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDMarshallerFactory {


    private Map<String, LSDMarshaler> marshallers;

    public void setMarshalers(final Map<String, LSDMarshaler> marshallers) {
        this.marshallers = marshallers;
    }

    public Map<String, LSDMarshaler> getMarshalers() {
        return marshallers;
    }
}
