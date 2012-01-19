package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import com.thoughtworks.xstream.XStream;

import javax.annotation.Nonnull;
import java.io.OutputStream;

/**
 * @author Neil Ellis
 */

public class XMLMarshaler implements LSDMarshaler {
    @Nonnull
    public String getMimeType() {
        return "text/xml";
    }

    public void marshal(final LSDTransferEntity lsdEntity, final OutputStream output) {
        final XStream xstream = LiquidXStreamFactory.getXstream();
        xstream.toXML(lsdEntity, output);
    }
}