package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDMarshaler;

import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDMarshallerFactory {


    private Map<String, LSDMarshaler> marshallers;

    public void setMarshalers(Map<String, LSDMarshaler> marshallers) {
        this.marshallers = marshallers;
    }

    public Map<String, LSDMarshaler> getMarshalers() {
        return marshallers;
    }
}
