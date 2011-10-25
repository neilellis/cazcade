package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDMarshaler;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import com.thoughtworks.xstream.XStream;

import java.io.OutputStream;

/**
 * @author Neil Ellis
 */

public class XMLMarshaler implements LSDMarshaler {

    public void marshal(LSDEntity lsdEntity, OutputStream output) {
        XStream xstream = LiquidXStreamFactory.getXstream();
        xstream.toXML(lsdEntity, output);
    }

    public String getMimeType() {
        return "text/xml";
    }

}