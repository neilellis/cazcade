/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.SimpleEntityFactory;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public class XMLUnmarshaler implements LSDUnmarshaler {
    private XMLInputFactory     xmlInputFactory;
    private SimpleEntityFactory lsdFactory;


    public void unmarshal(final Entity lsdEntity, final InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        LiquidXStreamFactory.getXstream().fromXML(input, lsdEntity);
    }

    @Nonnull
    public TransferEntity unmarshal(final InputStream input) {
        xmlInputFactory = XMLInputFactory.newInstance();
        return (TransferEntity) LiquidXStreamFactory.getXstream().fromXML(input);
    }

    public SimpleEntityFactory getLsdFactory() {
        return lsdFactory;
    }

    public void setLsdFactory(final SimpleEntityFactory lsdFactory) {
        this.lsdFactory = lsdFactory;
    }
}