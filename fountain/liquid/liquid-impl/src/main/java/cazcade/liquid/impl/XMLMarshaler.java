/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.TransferEntity;
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

    public void marshal(final TransferEntity lsdEntity, final OutputStream output) {
        final XStream xstream = LiquidXStreamFactory.getXstream();
        xstream.toXML(lsdEntity, output);
    }
}